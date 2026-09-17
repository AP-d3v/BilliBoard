import React from 'react'

// shared input styling so every field looks the same
const inputClass =
  'w-full rounded-md border border-rail-light bg-felt-dark px-3 py-2 text-cream outline-none focus:border-gold'

export default function LoginForm({ handleSubmit, formLabel, setEmail, setPassword }) {
  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <h1 className="font-display text-4xl uppercase text-cream">{formLabel}</h1>

      <div className="flex flex-col gap-1">
        <label htmlFor="email" className="font-mono text-xs uppercase text-cream/70">
          email
        </label>
        <input
          id="email" type="email" className={inputClass} onChange={(event) => setEmail(event.target.value)}
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="password" className="font-mono text-xs uppercase text-cream/70">
          password
        </label>
        <input
          id="password" type="password" className={inputClass}
          onChange={(event) => setPassword(event.target.value)}
        />
      </div>

      <button
        type="submit"
        className="mt-2 rounded-full bg-gold px-5 py-2 font-medium text-rail hover:brightness-110"
      >
        {formLabel}
      </button>
    </form>
  )
}
