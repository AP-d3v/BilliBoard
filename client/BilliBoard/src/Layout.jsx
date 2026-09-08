import React from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Header from './components/Header'

export default function Layout({ loggedInUser, onLogout }) {
  const location = useLocation()
  const message = location.state?.message

  return (
    <div className="min-h-screen bg-felt-dark">
      <Header loggedInUser={loggedInUser} onLogout={onLogout} />

      {message && (
        <p className="mx-auto max-w-6xl px-4 pt-4 font-mono text-sm text-gold">
          {message}
        </p>
      )}

      <main>
        <Outlet />
      </main>
    </div>
  )
}
