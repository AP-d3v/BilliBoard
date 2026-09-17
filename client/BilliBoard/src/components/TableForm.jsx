import React, { useState } from 'react'

const inputClass =
  'w-full rounded-md border border-rail-light bg-felt-dark px-3 py-2 text-cream outline-none focus:border-gold'

//conditional add or edit based on if we have a non null table
export default function TableForm({ table, onSubmit, onCancel }) {
  const [maxPlayers, setMaxPlayers] = useState(table ? table.maxPlayers : '')
  const [closingTime, setClosingTime] = useState(table ? table.closingTime : '')

  function handleSubmit(event) {
    event.preventDefault()
    onSubmit({ maxPlayers: Number(maxPlayers), closingTime })
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-col gap-3 rounded-lg border border-rail-light bg-rail/40 p-4"
    >
      <h3 className="font-display text-xl uppercase text-cream">
        {table ? 'Edit table' : 'Add a table'}
      </h3>

      <div className="flex flex-col gap-1">
        <label htmlFor="maxPlayers" className="font-mono text-xs uppercase text-cream/70">
          max players
        </label>
        <input
          id="maxPlayers"
          type="number"
          className={inputClass}
          value={maxPlayers}
          onChange={(event) => setMaxPlayers(event.target.value)}
          required
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="closingTime" className="font-mono text-xs uppercase text-cream/70">
          closing time
        </label>
        <input
          id="closingTime"
          type="time"
          className={inputClass}
          value={closingTime}
          onChange={(event) => setClosingTime(event.target.value)}
          required
        />
      </div>

      <div className="flex gap-2">
        <button
          type="submit"
          className="rounded-full bg-gold px-4 py-1.5 font-medium text-rail hover:brightness-110"
        >
          Save
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="rounded-full border border-cream/40 px-4 py-1.5 text-cream/80 hover:text-cream"
        >
          Cancel
        </button>
      </div>
    </form>
  )
}
