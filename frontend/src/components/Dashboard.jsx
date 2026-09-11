import { useEffect, useState } from 'react'
import { deposit, getTransactions, getWallet, transfer } from '../api.js'

const emptyDeposit = { amount: '', description: '' }
const emptyTransfer = { recipientEmail: '', amount: '', description: '' }

function formatMoney(value) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value)
}

function formatDate(value) {
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value))
}

function transactionLabel(type) {
  const labels = {
    DEPOSIT: 'Depósito',
    TRANSFER_IN: 'Transferência recebida',
    TRANSFER_OUT: 'Transferência enviada',
  }
  return labels[type] || type
}

function Dashboard({ onLogout }) {
  const [wallet, setWallet] = useState(null)
  const [transactions, setTransactions] = useState([])
  const [depositForm, setDepositForm] = useState(emptyDeposit)
  const [transferForm, setTransferForm] = useState(emptyTransfer)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadData()
  }, [])

  async function loadData() {
    try {
      const [walletData, historyData] = await Promise.all([getWallet(), getTransactions()])
      setWallet(walletData)
      setTransactions(historyData.transactions)
    } catch (requestError) {
      handleError(requestError)
    } finally {
      setLoading(false)
    }
  }

  function handleError(requestError) {
    if (requestError.status === 401) {
      onLogout()
      return
    }
    const fieldMessages = Object.values(requestError.fields || {})
    setError(fieldMessages[0] || requestError.message)
  }

  async function handleDeposit(event) {
    event.preventDefault()
    setSubmitting(true)
    setMessage('')
    setError('')

    try {
      await deposit(depositForm)
      setDepositForm(emptyDeposit)
      setMessage('Depósito realizado com sucesso.')
      await loadData()
    } catch (requestError) {
      handleError(requestError)
    } finally {
      setSubmitting(false)
    }
  }

  async function handleTransfer(event) {
    event.preventDefault()
    setSubmitting(true)
    setMessage('')
    setError('')

    try {
      await transfer(transferForm)
      setTransferForm(emptyTransfer)
      setMessage('Transferência realizada com sucesso.')
      await loadData()
    } catch (requestError) {
      handleError(requestError)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <div className="loading">Carregando...</div>
  }

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <div className="brand">Secure Wallet</div>
          <span className="header-subtitle">Painel da carteira</span>
        </div>
        <button className="secondary-button" onClick={onLogout}>Sair</button>
      </header>

      <section className="balance-card">
        <span>Saldo disponível</span>
        <strong>{formatMoney(wallet?.balance || 0)}</strong>
      </section>

      {message && <div className="success-message page-message">{message}</div>}
      {error && <div className="error-message page-message">{error}</div>}

      <section className="operation-grid">
        <div className="panel">
          <h2>Adicionar saldo</h2>
          <form onSubmit={handleDeposit}>
            <label>
              Valor
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={depositForm.amount}
                onChange={(event) => setDepositForm({ ...depositForm, amount: event.target.value })}
                required
              />
            </label>
            <label>
              Descrição
              <input
                value={depositForm.description}
                onChange={(event) => setDepositForm({ ...depositForm, description: event.target.value })}
                maxLength="120"
                placeholder="Opcional"
              />
            </label>
            <button className="primary-button" type="submit" disabled={submitting}>
              Depositar
            </button>
          </form>
        </div>

        <div className="panel">
          <h2>Fazer transferência</h2>
          <form onSubmit={handleTransfer}>
            <label>
              E-mail do destinatário
              <input
                type="email"
                value={transferForm.recipientEmail}
                onChange={(event) => setTransferForm({ ...transferForm, recipientEmail: event.target.value })}
                required
              />
            </label>
            <label>
              Valor
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={transferForm.amount}
                onChange={(event) => setTransferForm({ ...transferForm, amount: event.target.value })}
                required
              />
            </label>
            <label>
              Descrição
              <input
                value={transferForm.description}
                onChange={(event) => setTransferForm({ ...transferForm, description: event.target.value })}
                maxLength="120"
                placeholder="Opcional"
              />
            </label>
            <button className="primary-button" type="submit" disabled={submitting}>
              Transferir
            </button>
          </form>
        </div>
      </section>

      <section className="panel history-panel">
        <h2>Últimas movimentações</h2>
        {transactions.length === 0 ? (
          <p className="empty-state">Nenhuma movimentação registrada.</p>
        ) : (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Tipo</th>
                  <th>Descrição</th>
                  <th>Data</th>
                  <th>Valor</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((item) => {
                  const outgoing = item.type === 'TRANSFER_OUT'
                  return (
                    <tr key={item.id}>
                      <td>{transactionLabel(item.type)}</td>
                      <td>{item.description || item.relatedEmail || '-'}</td>
                      <td>{formatDate(item.createdAt)}</td>
                      <td className={outgoing ? 'amount-out' : 'amount-in'}>
                        {outgoing ? '- ' : '+ '}{formatMoney(item.amount)}
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </main>
  )
}

export default Dashboard
