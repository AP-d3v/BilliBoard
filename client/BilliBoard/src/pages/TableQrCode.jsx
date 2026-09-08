import React from 'react'
import { Link, useParams } from 'react-router-dom'
import QRCode from 'react-qr-code'

export default function TableQrCode() {
  const { tableId } = useParams()

  // get the root url + table id to generate the url , useparam to generate it for that specific table 
  const scanUrl = `${window.location.origin}/scan/${tableId}`

  return (
    <div className="mx-auto max-w-md px-4 py-10 text-center">
      <Link to="/dashboard" className="font-mono text-sm text-cream/60 hover:text-cream">
        back to dashboard
      </Link>

      <h1 className="mt-4 font-display text-3xl uppercase text-cream">Table #{tableId}</h1>
      <p className="font-mono text-xs text-cream/60">Scan to check in</p>

      <div className="mt-6 inline-block rounded-lg bg-white p-4">
        <QRCode value={scanUrl} />
      </div>
    </div>
  )
}
