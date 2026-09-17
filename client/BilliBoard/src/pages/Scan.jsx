import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import PoolTableDiv from '../components/PoolTableDiv'
import { getPushSubscriptionId } from '../onesignal'

const sessionKey = 'billiboardSession'

const inputClass =
  'w-full rounded-md border border-rail-light bg-felt-dark px-3 py-2 text-cream outline-none focus:border-gold'

function getSessionId() {
  const stored = localStorage.getItem(sessionKey)
  if (stored === null) {
    return ''
  }
  return stored
}

export default function Scan() {
  const { tableId } = useParams()
  const [board, setBoard] = useState({ currentPlayerName: null, confirmOpen: false, waiting: [] })
  const [role, setRole] = useState('NONE')
  const [playerName, setPlayerName] = useState('')
  const [patronEmail, setPatronEmail] = useState('')
  const [error, setError] = useState('')

  async function refresh() {
    const boardResponse = await fetch(`http://localhost:8080/tables/${tableId}/reservations`)
    if (boardResponse.ok) {
      setBoard(await boardResponse.json())
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

  async function handleJoin(event) {
    event.preventDefault()
    setError('')

    const onesignalSubscriptionId = await getPushSubscriptionId()

    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerName, patronEmail, sessionId: getSessionId(), onesignalSubscriptionId }),
    })
    const data = await response.json()
    if (response.ok) {
      localStorage.setItem(sessionKey, data.sessionId)
      setPlayerName('')
      setPatronEmail('')
      refresh()
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  async function handleLeave() {
    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: getSessionId() }),
    })
    if (response.ok) {
      refresh()
    }
  }

  async function handleNudge() {
    setError('')
    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations/nudge`, {
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

  async function handleCheckIn() {
    setError('')
    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations/check-in`, {
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
    <div className="mx-auto max-w-2xl px-4 py-10">
      <PoolTableDiv>
        <h1 className="font-display text-3xl uppercase text-cream">Table #{tableId}</h1>

        <p className="mt-2 font-mono text-xs text-cream/60">Now playing</p>
        <p className="font-mono text-sm text-cream">
          {board.currentPlayerName || 'nobody'}
          {board.confirmOpen && ' (asked to confirm)'}
        </p>

        <p className="mt-4 font-mono text-xs text-cream/60">Line</p>
        <ol className="mt-1 flex flex-col gap-1 font-mono text-sm text-cream">
          {board.waiting.map((patron, index) => (
            <li key={index}>
              {index + 1}. {patron.playerName}
            </li>
          ))}
          {board.waiting.length === 0 && <li className="text-cream/60">Nobody waiting.</li>}
        </ol>
      </PoolTableDiv>

      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}

      {role === 'CURRENT' ? (
        <p className="mt-6 text-center font-mono text-sm text-cream/70">
          You are the current player.
        </p>
      ) : role === 'CHECK_IN' ? (
        <div className="mt-6 text-center">
          <p className="font-mono text-sm text-cream/70">
            It&apos;s your turn. Check in within 2 minutes to take the table.
          </p>
          <button
            onClick={handleCheckIn}
            className="mt-2 rounded-full bg-gold px-5 py-2 font-medium text-rail hover:brightness-110"
          >
            Check in
          </button>
        </div>
      ) : role === 'NEXT' ? (
        <p className="mt-6 text-center font-mono text-sm text-cream/70">
          You are next in line for this table.
        </p>
      ) : role === 'WAITING' ? (
        <div className="mt-6 text-center">
          <p className="font-mono text-sm text-cream/70">
            You are in line. We&apos;ll reach out when it&apos;s your turn.
          </p>
          <button
            onClick={handleLeave}
            className="mt-2 rounded-full border border-cream/40 px-4 py-1.5 text-sm text-cream/80 hover:text-cream"
          >
            Leave the line
          </button>
        </div>
      ) : (
        <form onSubmit={handleJoin} className="mt-6 flex flex-col gap-3">
          <input
            className={inputClass}
            placeholder="your name"
            value={playerName}
            onChange={(event) => setPlayerName(event.target.value)}
            required
          />
          <input
            className={inputClass}
            type="email"
            placeholder="your email"
            value={patronEmail}
            onChange={(event) => setPatronEmail(event.target.value)}
            required
          />
          <button
            type="submit"
            className="rounded-full bg-gold px-5 py-2 font-medium text-rail hover:brightness-110"
          >
            Join the line
          </button>
        </form>
      )}

      {board.currentPlayerName && (role === 'NEXT' || role === 'WAITING') && (
        <div className="mt-4 text-center">
          <button
            onClick={handleNudge}
            className="rounded-full border border-cream/40 px-4 py-1.5 text-sm text-cream/80 hover:text-cream"
          >
            Table empty? Nudge current player
          </button>
        </div>
      )}

      <p className="mt-6 text-center font-mono text-xs text-cream/40">
        <Link to={`/still-here/${tableId}`}>still-here QR</Link>
        {' · '}
        <Link to={`/checkin/${tableId}`}>check-in QR</Link>
      </p>
    </div>
  )
}
