import React from 'react'
import { useParams } from 'react-router-dom'


export default function Scan() {
  const { tableId } = useParams()

  return (
    <div className="mx-auto max-w-md px-4 py-10 text-center">
      <h1 className="font-display text-3xl uppercase text-cream">Table #{tableId}</h1>
      <p className="mt-2 font-mono text-sm text-cream/60">Check-in coming soon.</p>
    </div>
  )
}
