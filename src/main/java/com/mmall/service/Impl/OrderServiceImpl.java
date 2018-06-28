package com.mmall.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午1:50 2018/6/26
 * @ Description：订单功能接口实现（包含支付）
 */

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public ServerResponse<Map<String, String>> payOrder(Long orderNo, Integer userId, String path) {
        /**
         * create by: vain
         * description: 对订单进行支付，支付宝会返回一个二维码，并将二维码图片保存在FTP服务器中.
         * create time: 下午6:14 2018/6/26
         *
         * @Param: orderNo
         * @Param: userId
         * @Param: path
         * @return com.mmall.common.ServerResponse<java.util.Map<java.lang.String,java.lang.String>>
         */
        Map<String, String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("支付失败，不存在该订单");
        }
        resultMap.put("orderNo", order.getOrderNo().toString());

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "vainmmall扫码支付,订单号" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单" + outTradeNo + "总计" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItems = orderItemMapper.selectByOrderNoUserId(orderNo, userId);
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        for (OrderItem orderItem : orderItems) {
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getId().toString(), orderItem.getProductName(), BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), 100d).longValue(), orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods);
        }


        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);


                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                // 需要修改为运行机器上的路径
                // 细节细节细节 获取的path后面是没有/的 拼接的话记得加
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                logger.info("filePath:" + qrPath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("二维码上传失败", e);
                }
                resultMap.put("qrPath", PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName());
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse checkAliCallback(Map<String, String> params) {
        /**
         * create by: vain
         * description: 验证回调是否由支付宝发出
         * create time: 下午6:48 2018/6/26
         *
         * @Param: params
         * @return com.mmall.common.ServerResponse
         */
        Long orderNo = Long.valueOf(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        if (order.getStatus() >= Const.OrderStatus.PAID.getCode()) {
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatus.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(orderNo);
        payInfo.setPayPlatform(Const.PayPlatform.Alipay.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<Boolean> queryOrderPayStatus(Integer userId, Long orderNo) {
        /**
         * create by: vain
         * description: 查看订单是否已支付
         * create time: 下午7:12 2018/6/26
         *
         * @Param: userId
         * @Param: orderNo
         * @return com.mmall.common.ServerResponse<java.lang.Boolean>
         */
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户并没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatus.PAID.getCode()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }

    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        /**
         * create by: vain
         * description: 创建订单
         * create time: 下午1:21 2018/6/27
         *
         * @Param: userId
         * @Param: shippingId
         * @return com.mmall.common.ServerResponse
         */
        List<Cart> cartList = cartMapper.selectCartCheckedByUserId(userId);
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("未选中任何商品");
        }
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = getOrderTotalPayment(orderItemList);
        Order order = assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单失败");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        orderItemMapper.batchInsertOrderItem(orderItemList);
        reduceProductStock(orderItemList);
        cleanCart(cartList);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        /**
         * create by: vain
         * description: 封装orderVo对象
         * create time: 下午1:21 2018/6/27
         *
         * @Param: order
         * @Param: orderItemList
         * @return com.mmall.vo.OrderVo
         */
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentType.codeOf(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatus.codeOf(order.getStatus()).getValue());
        orderVo.setPaymentTime(DateTimeUtil.dateToString(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToString(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToString(order.getEndTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToString(order.getCloseTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToString(order.getCreateTime()));
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }
        return orderVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        /**
         * create by: vain
         * description: 封装shippingVo对象
         * create time: 下午1:10 2018/6/27
         *
         * @Param: shipping
         * @return com.mmall.vo.ShippingVo
         */
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        /**
         * create by: vain
         * description: 封装orderItemVo对象
         * create time: 下午1:10 2018/6/27
         *
         * @Param: orderItem
         * @return com.mmall.vo.OrderItemVo
         */
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setCreateTime(DateTimeUtil.dateToString(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private void cleanCart(List<Cart> cartList) {
        /**
         * create by: vain
         * description: 将订单中的商品从购物车中清空
         * create time: 下午12:41 2018/6/27
         *
         * @Param: cartList
         * @return void
         */
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        /**
         * create by: vain
         * description: 更新商品库存
         * create time: 下午12:38 2018/6/27
         *
         * @Param: orderItemList
         * @return void
         */
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKey(product);
        }
    }

    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        /**
         * create by: vain
         * description: 获得订单中的物品明细
         * create time: 下午1:11 2018/6/27
         *
         * @Param: userId
         * @Param: cartList
         * @return com.mmall.common.ServerResponse
         */
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不在售卖状态");
            }
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    private BigDecimal getOrderTotalPayment(List<OrderItem> orderItemList) {
        /**
         * create by: vain
         * description: 计算订单支付总金额
         * create time: 下午1:13 2018/6/27
         *
         * @Param: orderItemList
         * @return java.math.BigDecimal
         */
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = payment.add(orderItem.getTotalPrice());
        }
        return payment;
    }

    private Long generateOrderNo() {
        /**
         * create by: vain
         * description: 生成订单号
         * create time: 下午1:13 2018/6/27
         *
         * @Param:
         * @return java.lang.Long
         */
        Long orderNo = System.currentTimeMillis();
        return orderNo + new Random().nextInt(100);
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        /**
         * create by: vain
         * description: 封装订单,并添加
         * create time: 下午1:14 2018/6/27
         *
         * @Param: userId
         * @Param: shippingId
         * @Param: payment
         * @return com.mmall.pojo.Order
         */
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(Const.PaymentType.ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(Const.OrderStatus.NO_PAY.getCode());
        int resultCount = orderMapper.insert(order);
        if (resultCount > 0) {
            return order;
        }
        return null;
    }

    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        /**
         * create by: vain
         * description: 取消订单
         * create time: 下午1:59 2018/6/27
         *
         * @Param: userId
         * @Param: orderNo
         * @return com.mmall.common.ServerResponse
         */
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户没有此订单");
        }
        if (order.getStatus() != Const.OrderStatus.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("此订单已付款，无法被取消");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatus.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId) {
        /**
         * create by: vain
         * description: 对orderProductVo类进行封装
         * create time: 下午2:16 2018/6/27
         *
         * @Param: userId
         * @return com.mmall.common.ServerResponse<com.mmall.vo.OrderProductVo>
         */
        List<Cart> cartList = cartMapper.selectCartCheckedByUserId(userId);
        if (cartList == null) {
            return ServerResponse.createByErrorMessage("未选任何商品");
        }
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        OrderProductVo orderProductVo = new OrderProductVo();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(getOrderTotalPayment(orderItemList));
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse<PageInfo> orderList(Integer userId, Integer pageNum, Integer pageSize) {
        /**
         * create by: vain
         * description: 查看订单列表
         * create time: 下午3:32 2018/6/27
         *
         * @Param: userId
         * @Param: pageNum
         * @Param: pageSize
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);

    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Order order : orderList) {
            if (userId == null) {
                //管理员查询时候不需要传userId
                orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            } else {
                orderItemList = orderItemMapper.selectByOrderNoUserId(order.getOrderNo(), userId);
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    public ServerResponse<OrderVo> orderDetail(Integer userId, Long orderNo) {
        /**
         * create by: vain
         * description: 获取订单详情
         * create time: 下午3:40 2018/6/27
         *
         * @Param: userId
         * @Param: orderNo
         * @return com.mmall.common.ServerResponse<com.mmall.vo.OrderVo>
         */
        Order order = orderMapper.selectByOrderNoUserId(orderNo, userId);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoUserId(orderNo, userId);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("找不到该订单");
    }


    //backend
    public ServerResponse<PageInfo> manageOrderList(Integer pageNum, Integer pageSize) {
        /**
         * create by: vain
         * description: 后台订单列表显示
         * create time: 下午4:06 2018/6/27
         *
         * @Param: pageNum
         * @Param: pageSize
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrders();
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, null);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse<PageInfo> manageOrderSearch(Long orderNo, Integer pageNum, Integer pageSize) {
        /**
         * create by: vain
         * description: 后台按订单号查询
         * create time: 下午4:42 2018/6/27
         *
         * @Param: orderNo
         * @Param: pageNum
         * @Param: pageSize
         * @return com.mmall.common.ServerResponse<com.github.pagehelper.PageInfo>
         */
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
            pageInfo.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("没有找到订单");
    }

    public ServerResponse<OrderVo> manageOrderDetail(Long orderNo) {
        /**
         * create by: vain
         * description: 后台查看订单详情
         * create time: 下午4:41 2018/6/27
         *
         * @Param: orderNo
         * @return com.mmall.common.ServerResponse<com.mmall.vo.OrderVo>
         */
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到订单");
    }

    public ServerResponse manageSendGoods(Long orderNo) {
        /**
         * create by: vain
         * description: 订单发货
         * create time: 下午4:41 2018/6/27
         *
         * @Param: orderNo
         * @return com.mmall.common.ServerResponse
         */
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            if (order.getStatus() == Const.OrderStatus.PAID.getCode()) {
                order.setStatus(Const.OrderStatus.SHIPPED.getCode());
                order.setSendTime(new Date());
                int resultCount = orderMapper.updateByPrimaryKeySelective(order);
                if (resultCount > 0) {
                    return ServerResponse.createBySuccess("发货成功");
                }
                return ServerResponse.createByErrorMessage("发货失败");
            }
        }
        return ServerResponse.createByErrorMessage("没有找到订单");
    }

}
