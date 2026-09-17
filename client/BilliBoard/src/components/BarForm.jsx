import React, { useState } from 'react'

const inputClass =
  'w-full rounded-md border border-rail-light bg-felt-dark px-3 py-2 text-cream outline-none focus:border-gold'


export default function BarForm({ bar, onSubmit, onCancel }) {
  const [barName, setBarName] = useState(bar ? bar.barName : '')
  const [address, setAddress] = useState(bar ? bar.address : '')

  function handleSubmit(event) {
    event.preventDefault()
    onSubmit({ barName, address })
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="flex flex-col gap-3 rounded-lg border border-rail-light bg-rail/40 p-4"
    >
      <h3 className="font-display text-xl uppercase text-cream">
        {bar ? 'Edit bar' : 'Add a bar'}
      </h3>

      <div className="flex flex-col gap-1">
        <label htmlFor="barName" className="font-mono text-xs uppercase text-cream/70">
          name
        </label>
        <input
          id="barName"
          className={inputClass}
          value={barName}
          onChange={(event) => setBarName(event.target.value)}
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="address" className="font-mono text-xs uppercase text-cream/70">
          address
        </label>
        <input
          id="address"
          className={inputClass}
          value={address}
          onChange={(event) => setAddress(event.target.value)}
        />
      </div>

      <div className="flex gap-2">
        <button
          type="submit"
          className="rounded-full bg-gold px-4 py-1.5 font-medium text-rail hover:brightness-110"
        >
          Save
        </button>
         {/* we passed in a arrow function that simply turns off the state of either adding or  edditing*/}
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
