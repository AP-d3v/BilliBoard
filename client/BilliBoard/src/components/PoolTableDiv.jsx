import React from 'react'


export default function PoolTableDiv({ children, height = "min-h-[32rem]", width = "w-full" }) {
  const pockets = ["-top-3 -left-3", "-top-3 -right-3", "-bottom-3 -left-3", "-bottom-3 -right-3"]
  return (
  <div className={`${height} ${width} bg-[radial-gradient(circle,transparent_91%,black)] relative border-[18px] border-rail rounded-[2.5rem] bg-felt shadow-2xl`}>
    {pockets.map((size) => <span key={size} className={`${size} absolute h-7 w-7 rounded-full bg-pocket`}></span>)}
  <div id='texture' className='absolute inset-0 rounded-[2rem]' style={{backgroundImage:  'repeating-linear-gradient(-45deg, #ffffff06 0 2px, transparent 2px 5px)'}}></div>
  <div id='content' className='relative z-10 p-8 md:p-16'>
    <div>{children}</div>
  </div>
  </div>)
}
