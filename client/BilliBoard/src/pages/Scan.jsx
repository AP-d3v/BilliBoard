import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import PoolTableDiv from '../components/PoolTableDiv'
//TODO get rid of conditionally rendered login sign out buttons
//TODO Stop user from having the abilty to create a random page by typing in a the link than any number.
// string for key of session
const sessionKey = 'billiboardSession'

const inputClass =
  'w-full rounded-md border border-rail-light bg-felt-dark px-3 py-2 text-cream outline-none focus:border-gold'

export default function Scan() {
  const { tableId } = useParams()
  const [line, setLine] = useState([])
  const [playerName, setPlayerName] = useState('')
  const [patronEmail, setPatronEmail] = useState('')
  const [error, setError] = useState('')
  // initial state of join key , diffrentiates who joined which table
  const joinedKey = `billiboardJoined:${tableId}`
  const savedJoined = localStorage.getItem(joinedKey)
  const [joined, setJoined] = useState(savedJoined === 'yes')

  async function loadLine() {
    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations`)
    if (response.ok) {
      setLine(await response.json())
    }
  }

  useEffect(() => {
    async function load() {
      await loadLine()
    }
    load()
    
  }, [tableId])



  async function handleJoin(event) {
    event.preventDefault()
    setError('')
    // send our saved session id, or an empty string if we don't have one yet
    let sessionId = localStorage.getItem(sessionKey)
    if (sessionId === null) {
      sessionId = ''
    }

    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerName, patronEmail, sessionId }),
    })
    const data = await response.json()
    //sets session key and joined key in local storage to yes
    if (response.ok) {
      localStorage.setItem(sessionKey, data.sessionId)
      localStorage.setItem(joinedKey, 'yes')
      setJoined(true)
      setPlayerName('')
      setPatronEmail('')
      loadLine()
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  async function handleLeave() {
    let sessionId = localStorage.getItem(sessionKey)
    if (sessionId === null) {
      sessionId = ''
    }

    const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId }),
    })
    if (response.ok) {
      localStorage.removeItem(joinedKey)
      setJoined(false)
      loadLine()
    }
  }

  return (
    <div className="mx-auto max-w-2xl px-4 py-10">
      <PoolTableDiv>
        <h1 className="font-display text-3xl uppercase text-cream">Table #{tableId}</h1>
        <p className="mt-1 font-mono text-xs text-cream/60">Line Order</p>
        <ol className="mt-4 flex flex-col gap-1 font-mono text-sm text-cream">
          {line.map((patron, index) => (
            <li key={index}>
              {index + 1}. {patron.playerName}
            </li>
          ))}
          {line.length === 0 && (
            <li className="text-cream/60">Nobody yet.</li>
          )}
        </ol>
      </PoolTableDiv>

      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}

      {joined ? (
        <div className="mt-6 text-center">
          <p className="font-mono text-sm text-cream/70">You are in line for this table.</p>
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
    </div>
  )
}
