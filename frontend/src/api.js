const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export function getToken() {
  return sessionStorage.getItem('secure_wallet_token')
}

export function saveToken(token) {
  sessionStorage.setItem('secure_wallet_token', token)
}

export function clearToken() {
  sessionStorage.removeItem('secure_wallet_token')
}

async function request(path, options = {}) {
  const headers = { ...options.headers }
  const token = getToken()

  if (options.body) {
    headers['Content-Type'] = 'application/json'
  }
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_URL}${path}`, { ...options, headers })
  const data = await response.json().catch(() => null)

  if (!response.ok) {
    const error = new Error(data?.message || 'Não foi possível concluir a operação')
    error.fields = data?.fields || {}
    error.status = response.status
    throw error
  }

  return data
}

export function registerUser(data) {
  return request('/api/users', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function login(data) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getWallet() {
  return request('/api/wallet')
}

export function getTransactions() {
  return request('/api/transactions?size=20')
}

export function deposit(data) {
  return request('/api/wallet/deposits', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function transfer(data) {
  return request('/api/transfers', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}
