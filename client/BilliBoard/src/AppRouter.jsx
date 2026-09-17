import React, { useEffect, useState } from 'react'
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import Layout from './Layout'
import Home from './pages/Home'
import Login from './pages/Login'
import SignUp from './pages/SignUp'
import NotFound from './components/NotFound'
import BarOwnerDashboard from './pages/BarOwnerDashboard'
import BarTables from './pages/BarTables'
import TableQrCode from './pages/TableQrCode'
import Scan from './pages/Scan'
import Checkin from './pages/Checkin'
import StillHere from './pages/StillHere'
import Done from './pages/Done'

export default function AppRouter() {
 
  const [loggedInUser, setLoggedInUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem('loggedInUser'))
    } catch {
      return null
    }
  })

  function handleLogin(barOwner) {
    setLoggedInUser(barOwner) //for state of website thats customized for this user
    localStorage.setItem('loggedInUser', JSON.stringify(barOwner))
  }
  function handleLogout() {
    setLoggedInUser(null)
    localStorage.removeItem('loggedInUser') // removes token after logout
  }

  const diyJwt = loggedInUser?.diyJwt
  useEffect(() => {
    if (!diyJwt) {
      return
    }
    fetch('http://localhost:8080/me', { headers: { Authorization: diyJwt } })
      .then((response) => {
        if (!response.ok) handleLogout()
      })
      .catch(() => {
        /* backend unreachable — leave the session alone */
      })
  }, [diyJwt])


  const router = createBrowserRouter([
    {
      path: '/',
      element: <Layout loggedInUser={loggedInUser} onLogout={handleLogout} />,
      errorElement: <NotFound />,
      children: [
        {
          index: true,
          element: loggedInUser ? <Navigate to="/dashboard" replace /> : <Home />,
        },
        {
          path: 'login',
          element: loggedInUser ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <Login onLogin={handleLogin} />
          ),
        },
        {
          path: 'signup',
          element: loggedInUser ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <SignUp onLogin={handleLogin} />
          ),
        },
        {
          path: 'dashboard',
          element: loggedInUser ? (
            <BarOwnerDashboard loggedInUser={loggedInUser} onLogout={handleLogout} />
          ) : (
            <Navigate
              to="/login"
              replace
              state={{ message: 'Please log in to see your dashboard.' }}
            />
          ),
        },
        {
          path: 'bars/:barId/tables',
          element: loggedInUser ? (
            <BarTables loggedInUser={loggedInUser} />
          ) : (
            <Navigate
              to="/login"
              replace
              state={{ message: 'Please log in to see your dashboard.' }}
            />
          ),
        },
        {
          path: 'tables/:tableId/qr',
          element: loggedInUser ? (
            <TableQrCode />
          ) : (
            <Navigate
              to="/login"
              replace
              state={{ message: 'Please log in to see your dashboard.' }}
            />
          ),
        },
        // route for patron facing checkin
        {
          path: 'scan/:tableId',
          element: <Scan />,
        },
        {
          path: 'checkin/:tableId',
          element: <Checkin />,
        },
        {
          path: 'still-here/:tableId',
          element: <StillHere />,
        },
        {
          path: 'done/:tableId',
          element: <Done />,
        },
        { path: '*', element: <NotFound /> },
      ],
    },
  ])

  return <RouterProvider router={router} />
}
