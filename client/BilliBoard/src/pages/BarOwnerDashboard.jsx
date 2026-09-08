import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import BarForm from '../components/BarForm'

export default function BarOwnerDashboard({ loggedInUser, onLogout }) {
  const [bars, setBars] = useState([])
  const [error, setError] = useState('')

  // state that manages the same form and conditionally renders based on if we want to add or edit 
  const [adding, setAdding] = useState(false)
  const [editingBar, setEditingBar] = useState(null)

  
  const authHeader = { Authorization: loggedInUser.diyJwt } // tp send the auth 

  async function loadBars() {
    const response = await fetch('http://localhost:8080/bars', { headers: authHeader })
    const data = await response.json()
    if (response.ok) {
      setBars(data)
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  // load the bars once when the dashboard opens
  useEffect(() => {
    async function load() {
      await loadBars()
    }
    load()
   
  }, [])
  //callback for bar add
  async function handleAdd({ barName, address }) {
    setError('') //clear any error msgs we might have had before
    const response = await fetch('http://localhost:8080/bars', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeader }, //"cracked open" authheader to send with bar object
      body: JSON.stringify({ barName, address }),
    })
    const data = await response.json()
    if (response.ok) {
      setAdding(false)
      loadBars()
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }
  //callback for update bar
  async function handleUpdate({ barName, address }) {
    setError('')
    const response = await fetch(`http://localhost:8080/bars/${editingBar.barId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...authHeader },
      body: JSON.stringify({ barName, address }),
    })
    const data = await response.json()
    if (response.ok) {
      setEditingBar(null)
      loadBars()
    } else {
      setError(Array.isArray(data) ? data.join(', ') : String(data))
    }
  }

  async function handleDelete(barId) {
    if (!window.confirm('Delete this bar and its tables?')) {
      return
    }
    const response = await fetch(`http://localhost:8080/bars/${barId}`, {
      method: 'DELETE',
      headers: authHeader,
    })
    if (response.ok) {
      loadBars()
    } else {
      setError('Could not delete that bar.')
    }
  }

  return (
    <div className="mx-auto max-w-4xl px-4 py-10">
      <div className="flex items-center justify-between">
        <h1 className="font-display text-4xl uppercase text-cream">
          Welcome, {loggedInUser.firstname}
        </h1>
        <button
          onClick={onLogout}
          className="rounded-full border border-cream/40 px-4 py-1.5 text-sm text-cream/80 hover:text-cream"
        >
          Sign out
        </button>
      </div>

      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}

      
      <section className="mt-10"> 
        <div className="flex items-center justify-between">
          <h2 className="font-display text-2xl uppercase text-cream">Your bars</h2>
          {/*conditionally renders add bar button if we are not currently editing or adding*/}
          {!adding && !editingBar && ( 
            <button
              onClick={() => setAdding(true)}
              className="rounded-full bg-gold px-4 py-1.5 text-sm font-medium text-rail hover:brightness-110"
            >
              Add a bar
            </button>
          )}
        </div>

        {adding && (
          <div className="mt-4">
            <BarForm onSubmit={handleAdd} onCancel={() => setAdding(false)} />
          </div>
        )}

        <ul className="mt-4 flex flex-col gap-3">
            {/*will either give you the edit version of the bar form or each bars edit and delete buttons*/}
          {bars.map((bar) =>
            editingBar && editingBar.barId === bar.barId ? (
              <li key={bar.barId}>
                <BarForm
                  bar={bar}
                  onSubmit={handleUpdate}
                  onCancel={() => setEditingBar(null)}
                />
              </li>
            ) : (
              <li
                key={bar.barId}
                className="flex items-center justify-between rounded-lg border border-rail-light bg-rail/40 px-4 py-3"
              >
                <Link to={`/bars/${bar.barId}/tables`} className="text-cream hover:text-gold">
                  <p className="font-medium">{bar.barName}</p>
                  <p className="font-mono text-xs text-cream/60">{bar.address}</p>
                </Link>
                <div className="flex gap-2 text-sm">
                  <button
                    onClick={() => setEditingBar(bar)}
                    className="text-cream/80 hover:text-cream"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(bar.barId)}
                    className="text-red-400 hover:text-red-300"
                  >
                    Delete
                  </button>
                </div>
              </li>
            ),
          )}
          {bars.length === 0 && !adding && (
            <li className="font-mono text-sm text-cream/60">No bars yet.</li>
          )}
        </ul>
        <p className="mt-3 font-mono text-xs text-cream/50">Click a bar to see its tables.</p>
      </section>
    </div>
  )
}
