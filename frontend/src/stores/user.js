import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, getUserId, getUsername, logout as authLogout } from '../utils/auth.js'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userId = ref(getUserId())
  const username = ref(getUsername())
  const isLoggedIn = ref(!!token.value)

  function setUserInfo(userInfo) {
    token.value = userInfo.token
    userId.value = userInfo.userId
    username.value = userInfo.username
    isLoggedIn.value = true
  }

  function logout() {
    authLogout()
    token.value = null
    userId.value = null
    username.value = null
    isLoggedIn.value = false
  }

  return {
    token,
    userId,
    username,
    isLoggedIn,
    setUserInfo,
    logout
  }
})
