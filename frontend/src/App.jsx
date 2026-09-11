import { useState } from 'react'
import { getToken } from './api.js'
import AuthPage from './components/AuthPage.jsx'

function App() {
  const [authenticated, setAuthenticated] = useState(Boolean(getToken()))

  if (!authenticated) {
    return <AuthPage onLogin={() => setAuthenticated(true)} />
  }

  return (
    <main className="page">
      <h1>Secure Wallet</h1>
      <p>Login realizado com sucesso.</p>
    </main>
  )
}

export default App
