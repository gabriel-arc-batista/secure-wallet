import { useState } from 'react'
import { login, registerUser, saveToken } from '../api.js'

const emptyLogin = { email: '', password: '' }
const emptyRegister = { fullName: '', email: '', password: '' }

function AuthPage({ onLogin }) {
  const [mode, setMode] = useState('login')
  const [loginForm, setLoginForm] = useState(emptyLogin)
  const [registerForm, setRegisterForm] = useState(emptyRegister)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function changeMode(nextMode) {
    setMode(nextMode)
    setMessage('')
    setError('')
  }

  async function handleLogin(event) {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      const response = await login(loginForm)
      saveToken(response.token)
      onLogin()
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleRegister(event) {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      await registerUser(registerForm)
      setRegisterForm(emptyRegister)
      setMode('login')
      setMessage('Conta criada. Faça login para continuar.')
    } catch (requestError) {
      const fieldMessages = Object.values(requestError.fields || {})
      setError(fieldMessages[0] || requestError.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="brand">Secure Wallet</div>
        <p className="subtitle">Carteira digital para controle de saldo e transferências.</p>

        <div className="tabs">
          <button className={mode === 'login' ? 'active' : ''} onClick={() => changeMode('login')}>
            Entrar
          </button>
          <button className={mode === 'register' ? 'active' : ''} onClick={() => changeMode('register')}>
            Criar conta
          </button>
        </div>

        {message && <div className="success-message">{message}</div>}
        {error && <div className="error-message">{error}</div>}

        {mode === 'login' ? (
          <form onSubmit={handleLogin}>
            <label>
              E-mail
              <input
                type="email"
                value={loginForm.email}
                onChange={(event) => setLoginForm({ ...loginForm, email: event.target.value })}
                required
              />
            </label>
            <label>
              Senha
              <input
                type="password"
                value={loginForm.password}
                onChange={(event) => setLoginForm({ ...loginForm, password: event.target.value })}
                required
              />
            </label>
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? 'Entrando...' : 'Entrar'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegister}>
            <label>
              Nome completo
              <input
                value={registerForm.fullName}
                onChange={(event) => setRegisterForm({ ...registerForm, fullName: event.target.value })}
                minLength="2"
                maxLength="120"
                required
              />
            </label>
            <label>
              E-mail
              <input
                type="email"
                value={registerForm.email}
                onChange={(event) => setRegisterForm({ ...registerForm, email: event.target.value })}
                required
              />
            </label>
            <label>
              Senha
              <input
                type="password"
                value={registerForm.password}
                onChange={(event) => setRegisterForm({ ...registerForm, password: event.target.value })}
                minLength="8"
                maxLength="72"
                required
              />
            </label>
            <button className="primary-button" type="submit" disabled={loading}>
              {loading ? 'Criando...' : 'Criar conta'}
            </button>
          </form>
        )}
      </section>
    </main>
  )
}

export default AuthPage
