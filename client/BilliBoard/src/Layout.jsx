import React from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Header from './components/Header'

const patronPaths = ['/scan/', '/checkin/', '/still-here/', '/done/']

export default function Layout({ loggedInUser, onLogout }) {
  const location = useLocation()
  const message = location.state?.message

  const isPatronPage = patronPaths.some((path) => location.pathname.startsWith(path))

  return (
    <div className="min-h-screen bg-felt-dark">
      {!isPatronPage && <Header loggedInUser={loggedInUser} onLogout={onLogout} />}

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
