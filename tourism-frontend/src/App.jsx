import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Footer from './components/Footer.jsx'
import ProtectedRoute from './routes/ProtectedRoute.jsx'

import Home from './pages/public/Home.jsx'
import Packages from './pages/public/Packages.jsx'
import PackageDetails from './pages/public/PackageDetails.jsx'
import Destinations from './pages/public/Destinations.jsx'
import TripPlanner from './pages/public/TripPlanner.jsx'
import About from './pages/public/About.jsx'
import Contact from './pages/public/Contact.jsx'
import NotFound from './pages/public/NotFound.jsx'

import Login from './pages/auth/Login.jsx'
import Register from './pages/auth/Register.jsx'

import CustomerDashboard from './pages/customer/Dashboard.jsx'
import Profile from './pages/customer/Profile.jsx'
import Booking from './pages/customer/Booking.jsx'
import MyBookings from './pages/customer/MyBookings.jsx'
import BookingDetails from './pages/customer/BookingDetails.jsx'
import Invoice from './pages/customer/Invoice.jsx'
import SubmitReview from './pages/customer/SubmitReview.jsx'
import SupportTickets from './pages/customer/SupportTickets.jsx'

import OperatorDashboard from './pages/operator/Dashboard.jsx'
import OperatorPackages from './pages/operator/Packages.jsx'
import PackageForm from './pages/operator/PackageForm.jsx'
import PackageBookings from './pages/operator/PackageBookings.jsx'

import AdminDashboard from './pages/admin/Dashboard.jsx'
import AdminCustomers from './pages/admin/Customers.jsx'
import AdminOperators from './pages/admin/Operators.jsx'
import AdminDestinations from './pages/admin/Destinations.jsx'
import AdminBookings from './pages/admin/Bookings.jsx'
import AdminSupportTickets from './pages/admin/SupportTickets.jsx'
import AdminFeedback from './pages/admin/Feedback.jsx'
import AdminReviews from './pages/admin/Reviews.jsx'
import Reports from './pages/admin/Reports.jsx'
import AuditLogs from './pages/admin/AuditLogs.jsx'

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        {/* Public */}
        <Route path="/" element={<Home />} />
        <Route path="/packages" element={<Packages />} />
        <Route path="/packages/:id" element={<PackageDetails />} />
        <Route path="/destinations" element={<Destinations />} />
        <Route path="/trip-planner" element={<TripPlanner />} />
        <Route path="/about" element={<About />} />
        <Route path="/contact" element={<Contact />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Any authenticated role */}
        <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />

        {/* Customer */}
        <Route path="/book/:scheduleId" element={<ProtectedRoute roles={['CUSTOMER']}><Booking /></ProtectedRoute>} />
        <Route path="/customer/dashboard" element={<ProtectedRoute roles={['CUSTOMER']}><CustomerDashboard /></ProtectedRoute>} />
        <Route path="/customer/bookings" element={<ProtectedRoute roles={['CUSTOMER']}><MyBookings /></ProtectedRoute>} />
        <Route path="/customer/bookings/:id" element={<ProtectedRoute roles={['CUSTOMER']}><BookingDetails /></ProtectedRoute>} />
        <Route path="/customer/bookings/:bookingId/invoice" element={<ProtectedRoute roles={['CUSTOMER']}><Invoice /></ProtectedRoute>} />
        <Route path="/customer/bookings/:id/review" element={<ProtectedRoute roles={['CUSTOMER']}><SubmitReview /></ProtectedRoute>} />
        <Route path="/customer/support-tickets" element={<ProtectedRoute roles={['CUSTOMER']}><SupportTickets /></ProtectedRoute>} />

        {/* Operator (+ Admin) */}
        <Route path="/operator/dashboard" element={<ProtectedRoute roles={['TOUR_OPERATOR', 'ADMIN']}><OperatorDashboard /></ProtectedRoute>} />
        <Route path="/operator/packages" element={<ProtectedRoute roles={['TOUR_OPERATOR', 'ADMIN']}><OperatorPackages /></ProtectedRoute>} />
        <Route path="/operator/packages/new" element={<ProtectedRoute roles={['TOUR_OPERATOR', 'ADMIN']}><PackageForm /></ProtectedRoute>} />
        <Route path="/operator/packages/:id/edit" element={<ProtectedRoute roles={['TOUR_OPERATOR', 'ADMIN']}><PackageForm /></ProtectedRoute>} />
        <Route path="/operator/packages/:id/bookings" element={<ProtectedRoute roles={['TOUR_OPERATOR', 'ADMIN']}><PackageBookings /></ProtectedRoute>} />

        {/* Admin only */}
        <Route path="/admin/dashboard" element={<ProtectedRoute roles={['ADMIN']}><AdminDashboard /></ProtectedRoute>} />
        <Route path="/admin/customers" element={<ProtectedRoute roles={['ADMIN']}><AdminCustomers /></ProtectedRoute>} />
        <Route path="/admin/operators" element={<ProtectedRoute roles={['ADMIN']}><AdminOperators /></ProtectedRoute>} />
        <Route path="/admin/destinations" element={<ProtectedRoute roles={['ADMIN']}><AdminDestinations /></ProtectedRoute>} />
        <Route path="/admin/bookings" element={<ProtectedRoute roles={['ADMIN']}><AdminBookings /></ProtectedRoute>} />
        <Route path="/admin/support-tickets" element={<ProtectedRoute roles={['ADMIN']}><AdminSupportTickets /></ProtectedRoute>} />
        <Route path="/admin/feedback" element={<ProtectedRoute roles={['ADMIN']}><AdminFeedback /></ProtectedRoute>} />
        <Route path="/admin/reviews" element={<ProtectedRoute roles={['ADMIN']}><AdminReviews /></ProtectedRoute>} />
        <Route path="/admin/reports" element={<ProtectedRoute roles={['ADMIN']}><Reports /></ProtectedRoute>} />
        <Route path="/admin/audit-logs" element={<ProtectedRoute roles={['ADMIN']}><AuditLogs /></ProtectedRoute>} />

        <Route path="*" element={<NotFound />} />
      </Routes>
      <Footer />
    </>
  )
}

export default App
