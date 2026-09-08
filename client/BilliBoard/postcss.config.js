// Tailwind v4 runs as a PostCSS plugin. Without this file the `@import
// "tailwindcss"` in index.css does nothing and no utility classes work.
export default {
  plugins: {
    '@tailwindcss/postcss': {},
  },
}
