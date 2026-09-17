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

export default function StillHere() {
  const { tableId } = useParams()
  const [role, setRole] = useState('')
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [error, setError] = useState('')

  async function refresh() {
    const boardResponse = await fetch(`http://localhost:8080/tables/${tableId}/reservations`)
    if (boardResponse.ok) {
      setConfirmOpen((await boardResponse.json()).confirmOpen)
    }
    const roleResponse = await fetch(`http://localhost:8080/tables/${tableId}/reservations/role`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: getSessionId() }),
    })
    if (roleResponse.ok) {
      setRole((await roleResponse.json()).role)
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
        <p className="mt-2 font-mono text-sm text-cream/70">Still here?</p>
      </PoolTableDiv>

      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}

      <div className="mt-6 font-mono text-sm text-cream/70">
        {role === 'CURRENT' && confirmOpen && (
          <div className="flex flex-col items-center gap-3">
            <p>Someone&apos;s waiting for the table. Are you still here?</p>
            <div className="flex gap-2">
              <button
                onClick={() => post('still-here')}
                className="rounded-full bg-gold px-4 py-1.5 font-medium text-rail hover:brightness-110"
              >
                Still playing
              </button>
              <button
                onClick={() => post('give-up')}
                className="rounded-full border border-cream/40 px-4 py-1.5 text-cream/80 hover:text-cream"
              >
                Done
              </button>
            </div>
          </div>
        )}

        {role === 'CURRENT' && !confirmOpen && (
          <p>You are the current player. Nothing to confirm right now.</p>
        )}

        {role === 'NEXT' && <p>You are the next player. Wait for the table to open up.</p>}

        {role === 'CHECK_IN' && (
          <p>
            It&apos;s your turn.{' '}
            <Link to={`/checkin/${tableId}`} className="text-gold hover:underline">
              Use the check-in QR
            </Link>{' '}
            to take the table.
          </p>
        )}

        {role !== 'CURRENT' && role !== 'NEXT' && role !== 'CHECK_IN' && (
          <p>
            This screen is for the current player.{' '}
            <Link to={`/scan/${tableId}`} className="text-gold hover:underline">
              Sign-in QR
            </Link>
          </p>
        )}
      </div>
    </div>
  )
}
