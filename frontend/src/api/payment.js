import request from '../utils/request.js'

export const createPayment = (data) => request.post('/pay/create', data)

export const getPaymentDetail = (orderId) => request.get(`/pay/detail/${orderId}`)
