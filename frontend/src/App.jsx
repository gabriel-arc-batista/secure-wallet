import { useState } from 'react'
import { clearToken, getToken } from './api.js'
import AuthPage from './components/AuthPage.jsx'
import Dashboard from './components/Dashboard.jsx'

function App() {
  const [authenticated, setAuthenticated] = useState(Boolean(getToken()))

  if (!authenticated) {
    return <AuthPage onLogin={() => setAuthenticated(true)} />
  }

  function logout() {
    clearToken()
    setAuthenticated(false)
  }

  return <Dashboard onLogout={logout} />
}

export default App
