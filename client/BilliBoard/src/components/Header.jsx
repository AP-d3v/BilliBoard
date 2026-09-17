import React from 'react'
import { Link } from 'react-router-dom'

export default function Header({ loggedInUser, onLogout }) {
  return (
    <header className="border-b border-rail-light/40 bg-rail">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4">
        {/* TODO add a logo */}
        <Link to="/" className="flex items-center gap-2 text-cream">
          <span className="h-3 w-3 rounded-full bg-gold" />
          <span className="font-display text-xl uppercase tracking-wide">BilliBoard</span>
        </Link>

        <nav className="flex items-center gap-4 text-sm">
          {loggedInUser == null ? (
            <>
              <Link to="/login" className="text-cream/80 hover:text-cream">
                Log in
              </Link>
              <Link
                to="/signup"
                className="rounded-full border border-gold px-4 py-1.5 font-medium text-gold hover:bg-gold hover:text-rail"
              >
                Create account
              </Link>
            </>
          ) : (
            <>
              <Link to="/dashboard" className="text-cream/80 hover:text-cream">
                Dashboard
              </Link>
              <button
                onClick={onLogout}
                className="rounded-full border border-cream/40 px-4 py-1.5 text-cream/80 hover:border-cream hover:text-cream"
              >
                Log out
              </button>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
