import request from '../utils/request.js'

export const createOrder = (data) => request.post('/order/create', data)

export const getOrderDetail = (id) => request.get(`/order/detail/${id}`)

export const getOrderList = (params) => request.get('/order/list', { params })

export const cancelOrder = (id) => request.put(`/order/${id}/cancel`)

export const confirmReceive = (id) => request.put(`/order/${id}/confirm`)
