import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'

const empty = { firstname: '', lastName: '', email: '', password: '' }

const inputClass =
  'w-full rounded-md border border-rail-light bg-felt-dark px-3 py-2 text-cream outline-none focus:border-gold'

export default function SignUp({ onLogin }) {
  const navigate = useNavigate()
  const [form, setForm] = useState(empty)
  const [errors, setErrors] = useState([])
// this call back function on event listener to update the state of the controlled component
  function handleChange(event) {
    //decontruct name and value attributes from controlled component.
    const { name, value } = event.target
    setForm((form) => ({ ...form, [name]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setErrors([])

    const response = await fetch('http://localhost:8080/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    })
    const data = await response.json()

    if (!response.ok) {
      setErrors(Array.isArray(data) ? data : [String(data)])
      return
    }

  
    const token = data.diyJwt
    const userJson = token.slice(0, token.lastIndexOf('|'))
    const barOwner = JSON.parse(userJson)
    barOwner.diyJwt = token

    onLogin(barOwner)
    navigate('/dashboard')
  }

  return (
    <div className="mx-auto max-w-sm px-4 py-16">
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <h1 className="font-display text-4xl uppercase text-cream">Create account</h1>

        {errors.length > 0 && (
          <ul className="font-mono text-sm text-red-400">
            {errors.map((e) => (
              <li key={e}>{e}</li>
            ))}
          </ul>
        )}

        <div className="flex flex-col gap-1">
          <label htmlFor="firstname" className="font-mono text-xs uppercase text-cream/70">
            first name
          </label>
          <input id="firstname" name="firstname" className={inputClass} value={form.firstname} onChange={handleChange} />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="lastName" className="font-mono text-xs uppercase text-cream/70">
            last name
          </label>
          <input id="lastName" name="lastName" className={inputClass} value={form.lastName} onChange={handleChange} />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="email" className="font-mono text-xs uppercase text-cream/70">
            email
          </label>
          <input id="email" name="email" type="email" className={inputClass} value={form.email} onChange={handleChange} />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="password" className="font-mono text-xs uppercase text-cream/70">
            password
          </label>
          <input
            id="password" name="password" type="password" className={inputClass} value={form.password}
            onChange={handleChange}
          />
        </div>

        <button
          type="submit"
          className="mt-2 rounded-full bg-gold px-5 py-2 font-medium text-rail hover:brightness-110"
        >
          Create account
        </button>
      </form>
    </div>
  )
}
