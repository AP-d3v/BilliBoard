import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import PoolTableDiv from '../components/PoolTableDiv'

const sessionKey = 'billiboardSession'

function getSessionId() {
  const stored = localStorage.getItem(sessionKey)
  if (stored === null) {
    return ''
  }
  return stored
}

export default function Checkin() {
  const { tableId } = useParams()
  const [role, setRole] = useState('')
  const [error, setError] = useState('')

  async function refresh() {
    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations/role`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: getSessionId() }),
    })
    if (response.ok) {
      setRole((await response.json()).role)
    }
  }

  useEffect(() => {
    async function load() {
      await refresh()
    }
    load()
  }, [tableId])

  async function post(path) {
    setError('')
    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations/${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: getSessionId() }),
    })
    if (response.ok) {
      refresh()
    } else {
      const data = await response.json()
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  return (
    <div className="mx-auto max-w-md px-4 py-10 text-center">
      <PoolTableDiv>
        <h1 className="font-display text-3xl uppercase text-cream">Table #{tableId}</h1>
        <p className="mt-2 font-mono text-sm text-cream/70">Check in</p>
      </PoolTableDiv>

      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}

      <div className="mt-6 font-mono text-sm text-cream/70">
        {role === 'CURRENT' && <p>You are the current player.</p>}

        {role === 'CHECK_IN' && (
          <div className="flex flex-col items-center gap-3">
            <p>It&apos;s your turn. Check in within 2 minutes to take the table.</p>
            <button
              onClick={() => post('check-in')}
              className="rounded-full bg-gold px-4 py-1.5 font-medium text-rail hover:brightness-110"
            >
              Check in
            </button>
          </div>
        )}

        {role === 'NEXT' && (
          <div className="flex flex-col items-center gap-3">
            <p>You are next in line.</p>
            <button
              onClick={() => post('nudge')}
              className="rounded-full border border-cream/40 px-4 py-1.5 text-cream/80 hover:text-cream"
            >
              Ask if the table is free
            </button>
            <p className="text-cream/50">
              This asks the current player to confirm they are still playing.
            </p>
          </div>
        )}

        {role === 'WAITING' && (
          <div className="flex flex-col items-center gap-3">
            <p>You are in line. We&apos;ll reach out when it&apos;s your turn.</p>
            <button
              onClick={() => post('nudge')}
              className="rounded-full border border-cream/40 px-4 py-1.5 text-cream/80 hover:text-cream"
            >
              Ask if the table is free
            </button>
            <p className="text-cream/50">
              This asks the current player to confirm they are still playing.
            </p>
          </div>
        )}

        {(role === 'NONE' || role === '') && (
          <p>
            <Link to={`/scan/${tableId}`} className="text-gold hover:underline">
              Scan the sign-in QR
            </Link>{' '}
            to join the line.
          </p>
        )}
      </div>
    </div>
  )
}
