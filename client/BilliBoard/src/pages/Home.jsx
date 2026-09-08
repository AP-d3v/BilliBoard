import React from 'react'
import PoolTableDiv from '../components/PoolTableDiv'

export default function Home() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-10">
      
      <PoolTableDiv height="min-h-[32rem]" width="w-full">
        <h1 className="max-w-2xl font-display text-3xl uppercase leading-none md:text-6xl">
          Becasue chalk is for the pool stick, <span className='text-gold'>not writing your name on a board...</span>
        </h1>
      </PoolTableDiv>

      <p className="mt-8 max-w-xl font-mono text-sm text-cream/80">
        BilliBoard is where bar owners keep track of their pool tables. Log in to
        manage your bars.
      </p>
    </div>
  )
}
