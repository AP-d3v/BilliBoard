import React from 'react'
import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-24 text-center">
      <h1 className="font-display text-7xl uppercase text-cream">404</h1>
      <p className="mt-2 font-mono text-sm text-cream/70">Page not found.</p>
      <Link to="/" className="mt-6 inline-block text-gold hover:underline">
        Back to home
      </Link>
    </div>
  )
}
