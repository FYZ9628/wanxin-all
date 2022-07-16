import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/uaa/oauth/token',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/uaa/oauth/check_token',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/uaa/oauth/logout',
    method: 'post'
  })
}
