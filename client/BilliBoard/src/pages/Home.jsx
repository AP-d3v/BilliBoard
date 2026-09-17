import React from 'react'
import PoolTableDiv from '../components/PoolTableDiv'

const cards = [
  {
    title: 'Virtual queues',
    text: 'Create and manage virtual queues for pool tables.',
  },
  {
    title: 'Notifications',
    text: 'Notify patrons when they can play.',
  },
  {
    title: 'QR code sign-in',
    text: 'No sign up or app download needed.',
  },
]

export default function Home() {
  return (
    <div className="mx-auto flex min-h-[calc(100vh-4.5rem)] max-w-6xl flex-col justify-center gap-8 px-4 py-6">
      <PoolTableDiv height="min-h-[18rem]" width="w-full">
        <h1 className="max-w-2xl font-display text-3xl uppercase leading-none md:text-5xl">
          Because chalk is for the pool sticks, <span className="text-gold">not writing your name on a board...</span>
        </h1>
      </PoolTableDiv>

      <div className="grid gap-4 sm:grid-cols-3">
        {cards.map((card) => (
          <div key={card.title} className="rounded-xl border border-rail-light/40 bg-rail p-5">
            <h2 className="font-display text-lg uppercase text-gold">{card.title}</h2>
            <p className="mt-2 font-mono text-sm text-cream/80">{card.text}</p>
          </div>
        ))}
      </div>
    </div>
  )
}
