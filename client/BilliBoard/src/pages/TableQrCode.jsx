import React from 'react'
import { Link, useParams } from 'react-router-dom'
import QRCode from 'react-qr-code'

export default function TableQrCode() {
  const { tableId } = useParams()

  const signInUrl = `${window.location.origin}/scan/${tableId}`
  const checkInUrl = `${window.location.origin}/checkin/${tableId}`
  const stillHereUrl = `${window.location.origin}/still-here/${tableId}`

  return (
    <div className="mx-auto max-w-md px-4 py-10 text-center">
      <Link to="/dashboard" className="font-mono text-sm text-cream/60 hover:text-cream">
        back to dashboard
      </Link>

      <h1 className="mt-4 font-display text-3xl uppercase text-cream">Table #{tableId}</h1>

      <p className="mt-6 font-mono text-xs uppercase text-cream/60">Sign-in QR (everyone)</p>
      <div className="mt-2 inline-block rounded-lg bg-white p-4">
        <QRCode value={signInUrl} />
      </div>

      <p className="mt-8 font-mono text-xs uppercase text-cream/60">Check-in QR</p>
      <div className="mt-2 inline-block rounded-lg bg-white p-4">
        <QRCode value={checkInUrl} />
      </div>

      <p className="mt-8 font-mono text-xs uppercase text-cream/60">Still-here QR (current player)</p>
      <div className="mt-2 inline-block rounded-lg bg-white p-4">
        <QRCode value={stillHereUrl} />
      </div>
    </div>
  )
}
