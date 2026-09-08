import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import LoginForm from '../components/LoginForm'


export default function Login({ onLogin }) {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')

    const response = await fetch('http://localhost:8080/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    })
    const data = await response.json()

    if (!response.ok) {
      //updates error state to display errors ifwe get any, check to see if its and array or just one error message conditionally 
      setError(Array.isArray(data) ? data.join(', ') : String(data))
      return
    }

    //saves our token for session auth
    const token = data.diyJwt
    const userJson = token.slice(0, token.lastIndexOf('|')) // uses elimiter to turn first part to user object in string 
    const barOwner = JSON.parse(userJson) // then turns it into an obejct.
    barOwner.diyJwt = token

    onLogin(barOwner)
    navigate('/dashboard')
  }

  return (
    <div className="mx-auto max-w-sm px-4 py-16">
      <LoginForm
        formLabel="Login" setEmail={setEmail} setPassword={setPassword}
        handleSubmit={handleSubmit}
      />
      {error && <p className="mt-4 font-mono text-sm text-red-400">{error}</p>}
    </div>
  )
}
