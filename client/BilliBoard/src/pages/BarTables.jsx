import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

export default function BarTables({ loggedInUser }) {
  const { barId } = useParams()
  const [bar, setBar] = useState(null)
  const [tables, setTables] = useState([])
  const [error, setError] = useState('')

  const authHeader = { Authorization: loggedInUser.diyJwt }

  useEffect(() => {
    async function load() {
      const barResponse = await fetch(`http://localhost:8080/bars/${barId}`, { headers: authHeader })
      if (!barResponse.ok) {
        setError('Could not load this bar.')
        return
      }
      setBar(await barResponse.json())

      const tablesResponse = await fetch(`http://localhost:8080/bars/${barId}/tables`, { headers: authHeader })
      setTables(await tablesResponse.json())
    }
    load()
    
  }, [barId])

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

      <ul className="mt-8 flex flex-col gap-2">
        {tables.map((table) => (
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
            <Link
              to={`/tables/${table.tableId}/qr`}
              className="rounded-full border border-gold px-3 py-1 text-gold hover:bg-gold hover:text-rail"
            >
              QR code
            </Link>
          </li>
        ))}
      </ul>

      {tables.length === 0 && !error && (
        <p className="mt-4 font-mono text-sm text-cream/60">No tables at this bar yet.</p>
      )}
    </div>
  )
}
