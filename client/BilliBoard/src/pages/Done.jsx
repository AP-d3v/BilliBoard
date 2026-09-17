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

export default function Done() {
  const { tableId } = useParams()
  const [message, setMessage] = useState('Giving up the table...')

  useEffect(() => {
    async function giveUp() {
      const response = await fetch(`http://localhost:8080/tables/${tableId}/reservations/give-up`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: getSessionId() }),
      })
      if (response.ok) {
        setMessage('You gave up the table. Thanks for playing!')
      } else {
        const data = await response.json()
        setMessage(Array.isArray(data) ? data.join(', ') : String(data))
      }
    }
    giveUp()
  }, [tableId])

  return (
    <div className="mx-auto max-w-md px-4 py-10 text-center">
      <PoolTableDiv>
        <h1 className="font-display text-3xl uppercase text-cream">Table #{tableId}</h1>
        <p className="mt-2 font-mono text-sm text-cream/70">{message}</p>
      </PoolTableDiv>

      <p className="mt-6 text-center font-mono text-xs text-cream/40">
        <Link to={`/scan/${tableId}`}>back to the table</Link>
      </p>
    </div>
  )
}
