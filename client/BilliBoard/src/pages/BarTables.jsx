import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import TableForm from '../components/TableForm'

export default function BarTables({ loggedInUser }) {
  const { barId } = useParams()
  const [bar, setBar] = useState(null)
  const [tables, setTables] = useState([])
  const [error, setError] = useState('')

  // state for managing whether form is editting or deleting 
  const [adding, setAdding] = useState(false)
  const [editingTable, setEditingTable] = useState(null)

  const authHeader = { Authorization: loggedInUser.diyJwt }

  async function loadTables() {
    const response = await fetch(`http://localhost:8080/bars/${barId}/tables`, { headers: authHeader })
    const data = await response.json()
    if (response.ok) {
      setTables(data)
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  useEffect(() => {
    async function load() {
      const barResponse = await fetch(`http://localhost:8080/bars/${barId}`, { headers: authHeader })
      if (!barResponse.ok) {
        setError('Could not load this bar.')
        return
      }
      setBar(await barResponse.json())
      await loadTables()
    }
    load()
    
  }, [barId])

  async function handleAdd({ maxPlayers, closingTime }) {
    setError('')
    const response = await fetch(`http://localhost:8080/bars/${barId}/tables`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeader },
      body: JSON.stringify({ maxPlayers, closingTime }),
    })
    const data = await response.json()
    if (response.ok) {
      setAdding(false)
      loadTables()
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  async function handleUpdate({ maxPlayers, closingTime }) {
    setError('')
    const response = await fetch(`http://localhost:8080/bars/${barId}/tables/${editingTable.tableId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...authHeader },
      body: JSON.stringify({ maxPlayers, closingTime }),
    })
    const data = await response.json()
    if (response.ok) {
      setEditingTable(null)
      loadTables()
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  async function handleDelete(tableId) {
    if (!window.confirm('Delete this table?')) {
      return
    }
    const response = await fetch(`http://localhost:8080/bars/${barId}/tables/${tableId}`, {
      method: 'DELETE',
      headers: authHeader,
    })
    if (response.ok) {
      loadTables()
    } else {
      setError('Could not delete that table.')
    }
  }

  return (
    <div className="mx-auto max-w-4xl px-4 py-10">
      <Link to="/dashboard" className="font-mono text-sm text-cream/60 hover:text-cream">
        back to dashboard
      </Link>

      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}

      {bar && (
        <div className="mt-4">
          <h1 className="font-display text-4xl uppercase text-cream">{bar.barName}</h1>
          <p className="font-mono text-xs text-cream/60">{bar.address}</p>
        </div>
      )}

      <div className="mt-8 flex items-center justify-between">
        <h2 className="font-display text-2xl uppercase text-cream">Tables</h2>
        {!adding && !editingTable && (
          <button
            onClick={() => setAdding(true)} className="rounded-full bg-gold px-4 py-1.5 text-sm font-medium text-rail hover:brightness-110"
          >
            Add a table
          </button>
        )}
      </div>

      {adding && (
        <div className="mt-4">
          <TableForm onSubmit={handleAdd} onCancel={() => setAdding(false)} />
        </div>
      )}
      <ul className="mt-4 flex flex-col gap-2">
        {tables.map((table) =>
          editingTable && editingTable.tableId === table.tableId ? (
            <li key={table.tableId}>
              <TableForm
              table={table} onSubmit={handleUpdate}
              onCancel={() => setEditingTable(null)}
              />
            </li>
          ) : (
            <li
              key={table.tableId}
              className="flex items-center justify-between rounded-lg border border-rail-light bg-rail/40 px-4 py-3 text-sm"
            >
              <div>
                <p className="text-cream">Table #{table.tableId}</p>
                <p className="font-mono text-cream/60">
                  max players {table.maxPlayers}, closes {table.closingTime}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <Link
                  to={`/tables/${table.tableId}/qr`}
                  className="rounded-full border border-gold px-3 py-1 text-gold hover:bg-gold hover:text-rail"
                >
                  QR code
                </Link>
                <button
                  onClick={() => setEditingTable(table)}
                  className="text-cream/80 hover:text-cream"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDelete(table.tableId)}
                  className="text-red-400 hover:text-red-300"
                >
                  Delete
                </button>
              </div>
            </li>
          ),
        )}
      </ul>

      {tables.length === 0 && !adding && !error && (
        <p className="mt-4 font-mono text-sm text-cream/60">No tables at this bar yet.</p>
      )}
    </div>
  )
}
