function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

//视频代码
//腾讯云短信服务
function sendMsgApi(data,code){
    return $axios({
        'url':'/user/sendMsg',
        'method':'post',
        data
    })
}
  