import React, { useState, useEffect } from 'react';
import {
  Lock, User, Sparkles, Database, Users,
  FileText, MessageSquare, LogOut, Search, Plus, Send, Download
} from 'lucide-react';
import './App.css';

const API_BASE_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8080'
  : 'https://medi-ai-sales-backend.onrender.com';

export default function App() {
  // --- STATE VARIABLES ---
  const [token, setToken] = useState(localStorage.getItem('jwt_token') || '');
  const [username, setUsername] = useState(localStorage.getItem('jwt_user') || '');
  const [activeTab, setActiveTab] = useState('inventory');

  // Auth Mode State: 'landing', 'login', 'register', 'forgot-password'
  const [authMode, setAuthMode] = useState('landing');
  const [currentSlide, setCurrentSlide] = useState(0);

  // Auto-rotating slides for features showcase
  useEffect(() => {
    if (authMode === 'landing') {
      const interval = setInterval(() => {
        setCurrentSlide(prev => (prev + 1) % 3);
      }, 3000);
      return () => clearInterval(interval);
    }
  }, [authMode]);

  // Error & Success Alert States
  const [authError, setAuthError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  // Login Form States
  const [loginUsername, setLoginUsername] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  // Register Form States
  const [regUsername, setRegUsername] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPhone, setRegPhone] = useState('');
  const [regOtp, setRegOtp] = useState('');
  const [regRole, setRegRole] = useState('OPERATOR');
  const [regOtpSent, setRegOtpSent] = useState(false);

  // Forgot Password States
  const [resetEmail, setResetEmail] = useState('');
  const [resetOtp, setResetOtp] = useState('');
  const [resetPasswordVal, setResetPasswordVal] = useState('');
  const [resetOtpSent, setResetOtpSent] = useState(false);
  const [otpLoading, setOtpLoading] = useState(false);
  const [resetOtpLoading, setResetOtpLoading] = useState(false);

  // Database Record States
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  // Billing (Invoice) States
  const [selectedCustomerId, setSelectedCustomerId] = useState('');
  const [cart, setCart] = useState([]); // [{ productId, name, rate, quantity, maxQty }]

  // AI Chat States
  const [chatInput, setChatInput] = useState('');
  const [chatHistory, setChatHistory] = useState([
    { sender: 'ai', text: 'Hi! I am your Medi-AI Assistant. Ask me anything about stock inventory or customer profiles!' }
  ]);
  const [aiLoading, setAiLoading] = useState(false);

  // --- AUTOMATIC REFRESH (FETCH DATA ON STARTUP) ---
  useEffect(() => {
    if (token) {
      fetchProducts();
      fetchCustomers();
    }
  }, [token]);

  // --- RESET ALL AUTH FORM MESSAGES ON TOGGLE ---
  const switchAuthMode = (mode) => {
    setAuthMode(mode);
    setAuthError('');
    setSuccessMsg('');
    setRegOtpSent(false);
    setResetOtpSent(false);
  };

  // --- HELPER: PARSE ERROR MESSAGE FROM SPRING BOOT ---
  const getErrorMessage = async (response) => {
    try {
      const data = await response.clone().json();
      return data.message || 'Operation failed.';
    } catch (e) {
      try {
        const text = await response.clone().text();
        return text || 'Operation failed.';
      } catch (e2) {
        return 'Operation failed.';
      }
    }
  };

  // --- API CALL: LOGIN ---
  const handleLogin = async (e) => {
    e.preventDefault();
    setAuthError('');
    setSuccessMsg('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: loginUsername, password: loginPassword })
      });

      if (!response.ok) {
        const errorMsg = await getErrorMessage(response);
        throw new Error(errorMsg || 'Invalid username or password');
      }

      const data = await response.json();

      // Save key credentials to browser storage (LocalStorage)
      localStorage.setItem('jwt_token', data.token);
      localStorage.setItem('jwt_user', data.username);

      setToken(data.token);
      setUsername(data.username);
    } catch (err) {
      setAuthError(err.message);
    }
  };

  // --- API CALL: SEND REGISTRATION OTP ---
  const handleSendRegOtp = async (e) => {
    e.preventDefault();
    setAuthError('');
    setSuccessMsg('');

    if (!regUsername || !regEmail) {
      setAuthError('Please fill in username and email first to request an OTP.');
      return;
    }

    setOtpLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/register/send-otp`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: regEmail, username: regUsername })
      });

      if (!response.ok) {
        const errorMsg = await getErrorMessage(response);
        throw new Error(errorMsg || 'Failed to send verification OTP.');
      }

      setRegOtpSent(true);
      setSuccessMsg('OTP code sent successfully!');
    } catch (err) {
      setAuthError(err.message);
    } finally {
      setOtpLoading(false);
    }
  };

  // --- API CALL: REGISTER USER WITH OTP ---
  const handleRegister = async (e) => {
    e.preventDefault();
    setAuthError('');
    setSuccessMsg('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: regUsername,
          password: regPassword,
          email: regEmail,
          phone: regPhone,
          otp: regOtp,
          role: regRole
        })
      });

      if (!response.ok) {
        const errorMsg = await getErrorMessage(response);
        throw new Error(errorMsg || 'Registration failed. Check details or OTP.');
      }

      const data = await response.json();

      // Auto login on successful registration
      localStorage.setItem('jwt_token', data.token);
      localStorage.setItem('jwt_user', data.username);
      setToken(data.token);
      setUsername(data.username);
    } catch (err) {
      setAuthError(err.message);
    }
  };

  // --- API CALL: SEND PASSWORD RECOVERY OTP ---
  const handleSendResetOtp = async (e) => {
    e.preventDefault();
    setAuthError('');
    setSuccessMsg('');

    if (!resetEmail) {
      setAuthError('Please enter your email to receive recovery code.');
      return;
    }

    setResetOtpLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/forgot-password/send-otp`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: resetEmail })
      });

      if (!response.ok) {
        const errorMsg = await getErrorMessage(response);
        throw new Error(errorMsg || 'No account found with this email.');
      }

      setResetOtpSent(true);
      setSuccessMsg('Recovery code sent successfully!');
    } catch (err) {
      setAuthError(err.message);
    } finally {
      setResetOtpLoading(false);
    }
  };

  // --- API CALL: COMPLETE PASSWORD RESET ---
  const handleResetPassword = async (e) => {
    e.preventDefault();
    setAuthError('');
    setSuccessMsg('');

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/forgot-password/reset`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: resetEmail,
          otp: resetOtp,
          newPassword: resetPasswordVal
        })
      });

      if (!response.ok) {
        const errorMsg = await getErrorMessage(response);
        throw new Error(errorMsg || 'Reset failed. Check OTP code.');
      }

      setSuccessMsg('Password updated successfully! You can now sign in.');
      setTimeout(() => switchAuthMode('login'), 2000);
    } catch (err) {
      setAuthError(err.message);
    }
  };

  // --- LOGOUT ---
  const handleLogout = () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
    setToken('');
    setUsername('');
    setCart([]);
    setSelectedCustomerId('');
    switchAuthMode('login');
  };

  // --- API CALL: FETCH PRODUCTS ---
  const fetchProducts = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/products`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setProducts(data);
      }
    } catch (err) {
      console.error('Error fetching products', err);
    }
  };

  // --- API CALL: FETCH CUSTOMERS ---
  const fetchCustomers = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/customers`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setCustomers(data);
      }
    } catch (err) {
      console.error('Error fetching customers', err);
    }
  };

  // --- API CALL: CHAT WITH LOCAL AI ---
  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!chatInput.trim() || aiLoading) return;

    const userMessage = chatInput;
    setChatInput('');
    setChatHistory(prev => [...prev, { sender: 'user', text: userMessage }]);
    setAiLoading(true);

    try {
      const response = await fetch(`${API_BASE_URL}/api/ai/chat?message=${encodeURIComponent(userMessage)}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      const reply = await response.text();
      setChatHistory(prev => [...prev, { sender: 'ai', text: reply }]);
    } catch (err) {
      setChatHistory(prev => [...prev, { sender: 'ai', text: 'Error connecting to AI backend.' }]);
    } finally {
      setAiLoading(false);
    }
  };

  // --- BILLING: ADD PRODUCT TO CART ---
  const addToCart = (product) => {
    const existing = cart.find(item => item.productId === product.id);
    if (existing) {
      if (existing.quantity >= product.quantity) return; // Cannot exceed stock
      setCart(cart.map(item =>
        item.productId === product.id
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      if (product.quantity <= 0) return; // Out of stock
      setCart([...cart, {
        productId: product.id,
        name: product.name,
        rate: product.rate,
        quantity: 1,
        maxQty: product.quantity
      }]);
    }
  };

  // --- BILLING: GENERATE INVOICE & DOWNLOAD PDF ---
  const handleGenerateInvoice = async () => {
    if (!selectedCustomerId || cart.length === 0) return;

    const payload = {
      customerId: parseInt(selectedCustomerId),
      items: cart.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    };

    try {
      // 1. Post invoice to save in DB and deduct stock
      const response = await fetch(`${API_BASE_URL}/api/invoices`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) throw new Error('Invoice processing failed');
      const invoiceData = await response.json();

      // 2. Fetch the raw PDF binary stream from our download endpoint
      const pdfResponse = await fetch(`${API_BASE_URL}/api/invoices/${invoiceData.id}/pdf`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!pdfResponse.ok) throw new Error('PDF retrieval failed');
      const blob = await pdfResponse.blob();

      // 3. Trigger clean binary file download in user browser
      const fileUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = fileUrl;
      link.setAttribute('download', `invoice-${invoiceData.id}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);

      // Clean up States
      setCart([]);
      setSelectedCustomerId('');
      fetchProducts(); // Refresh stock numbers
      alert('Invoice created successfully! PDF downloaded.');
    } catch (err) {
      alert('Error creating invoice: ' + err.message);
    }
  };

  // --- RENDER LOGIN / REGISTER / FORGOT PASSWORD / LANDING IF NOT AUTHENTICATED ---
  if (!token) {
    const slides = [
      {
        title: "Smart Inventory Tracking",
        description: "Monitor live medicine stocks, verify batch categories, and get automated low-stock metrics.",
        icon: <Database className="slide-icon" />
      },
      {
        title: "Instant PDF Invoicing",
        description: "Calculate subtotals, apply customized taxes and discounts, and stream formatted invoices directly in one click.",
        icon: <FileText className="slide-icon" />
      },
      {
        title: "Generative AI Assistant",
        description: "Run automated local RAG queries to ask your custom database about inventory in natural language.",
        icon: <Sparkles className="slide-icon" />
      }
    ];

    return (
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        {/* LANDING PAGE DEFAULT VIEW */}
        {authMode === 'landing' && (
          <div className="landing-container">
            <header className="landing-header">
              <div className="landing-logo text-gradient">
                <Sparkles /> Medi-AI-Sales
              </div>
            </header>

            <div className="landing-hero">
              <h1 className="landing-title text-gradient">The Smart Future of<br />Pharmacy Management</h1>
              <p className="landing-subtitle">
                A secure, state-of-the-art ERP system combining deep transactional inventory metrics with a localized AI RAG engine.
              </p>

              {/* Feature Slideshow Carousel */}
              <div className="slider-container glass-panel">
                {slides.map((slide, idx) => (
                  <div key={idx} className={`slide ${idx === currentSlide ? 'active' : ''}`}>
                    {slide.icon}
                    <h3>{slide.title}</h3>
                    <p>{slide.description}</p>
                  </div>
                ))}
              </div>

              {/* Action Buttons */}
              <div className="landing-actions">
                <button className="btn-primary" onClick={() => switchAuthMode('login')}>
                  Sign In to Store
                </button>
                <button className="btn-secondary" style={{ width: '100%', padding: '12px' }} onClick={() => switchAuthMode('register')}>
                  Register Company
                </button>
              </div>
            </div>

            <footer className="landing-footer">
              © {new Date().getFullYear()} Medi-AI-Sales ERP. Built for modern medical inventory operations.
            </footer>
          </div>
        )}

        {/* AUTH FORMS (LOGIN / REGISTER / FORGOT PASSWORD) */}
        {authMode !== 'landing' && (
          <div className="login-container">
            <div className="login-box glass-panel">
              {/* Back to Home Button */}
              <button className="back-to-home-btn" onClick={() => switchAuthMode('landing')}>
                ← Back to Home
              </button>

              <div className="login-header">
                <h2 className="text-gradient">Medi-AI-Sales</h2>
                <p>
                  {authMode === 'login' && 'Access your store dashboard'}
                  {authMode === 'register' && 'Create your company account'}
                  {authMode === 'forgot-password' && 'Recover your dashboard access'}
                </p>
              </div>

              {authError && <div className="login-error">{authError}</div>}
              {successMsg && <div className="success-message">{successMsg}</div>}

              {/* FLOW A: LOGIN MODE */}
              {authMode === 'login' && (
                <form onSubmit={handleLogin}>
                  <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <div className="input-icon-wrapper">
                      <User className="input-icon" />
                      <input 
                        type="text" 
                        id="username" 
                        className="form-input" 
                        placeholder="Enter username"
                        value={loginUsername}
                        onChange={(e) => setLoginUsername(e.target.value)}
                        required 
                      />
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <div className="input-icon-wrapper">
                      <Lock className="input-icon" />
                      <input 
                        type="password" 
                        id="password" 
                        className="form-input" 
                        placeholder="Enter password"
                        value={loginPassword}
                        onChange={(e) => setLoginPassword(e.target.value)}
                        required 
                      />
                    </div>
                  </div>
                  <button type="submit" className="btn-primary">
                    Sign In
                  </button>
                  
                  <div className="auth-footer">
                    <span className="auth-toggle-link" onClick={() => switchAuthMode('forgot-password')}>Forgot Password?</span>
                    <span className="auth-toggle-link" onClick={() => switchAuthMode('register')}>Create Account</span>
                  </div>
                </form>
              )}

              {/* FLOW B: REGISTER MODE */}
              {authMode === 'register' && (
                <form onSubmit={handleRegister}>
                  <div className="form-group">
                    <label>Username</label>
                    <div className="input-icon-wrapper">
                      <User className="input-icon" />
                      <input 
                        type="text" 
                        className="form-input" 
                        placeholder="Create username"
                        value={regUsername}
                        onChange={(e) => setRegUsername(e.target.value)}
                        required 
                      />
                    </div>
                  </div>

                  <div className="form-group">
                    <label>Email Address</label>
                    <div className="otp-group">
                      <div className="input-icon-wrapper" style={{ flexGrow: 1 }}>
                        <input 
                          type="email" 
                          className="form-input" 
                          style={{ paddingLeft: '14px' }}
                          placeholder="name@company.com"
                          value={regEmail}
                          onChange={(e) => setRegEmail(e.target.value)}
                          required 
                        />
                      </div>
                      <button 
                        type="button" 
                        className="btn-secondary" 
                        onClick={handleSendRegOtp}
                        disabled={otpLoading}
                      >
                        {otpLoading ? 'Sending...' : 'Send OTP'}
                      </button>
                    </div>
                  </div>

                  {regOtpSent && (
                    <div className="form-group" style={{ animation: 'fadeIn 0.3s ease-out' }}>
                      <label>Verification OTP</label>
                      <div className="input-icon-wrapper">
                        <Lock className="input-icon" />
                        <input 
                          type="text" 
                          className="form-input" 
                          placeholder="Enter 6-digit OTP code"
                          value={regOtp}
                          onChange={(e) => setRegOtp(e.target.value)}
                          maxLength={6}
                          required 
                        />
                      </div>
                    </div>
                  )}

                  <div className="form-group">
                    <label>Phone Number</label>
                    <div className="input-icon-wrapper">
                      <input 
                        type="text" 
                        className="form-input" 
                        style={{ paddingLeft: '14px' }}
                        placeholder="Enter phone number"
                        value={regPhone}
                        onChange={(e) => setRegPhone(e.target.value)}
                        required 
                      />
                    </div>
                  </div>

                  <div className="form-group">
                    <label>Password</label>
                    <div className="input-icon-wrapper">
                      <Lock className="input-icon" />
                      <input 
                        type="password" 
                        className="form-input" 
                        placeholder="Create password"
                        value={regPassword}
                        onChange={(e) => setRegPassword(e.target.value)}
                        required 
                      />
                    </div>
                  </div>

                  <div className="form-group">
                    <label>Account Role</label>
                    <select 
                      className="form-input" 
                      style={{ paddingLeft: '12px' }}
                      value={regRole}
                      onChange={(e) => setRegRole(e.target.value)}
                    >
                      <option value="OPERATOR">Store Operator</option>
                      <option value="ADMIN">System Administrator</option>
                    </select>
                  </div>

                  <button type="submit" className="btn-primary" disabled={!regOtpSent}>
                    Confirm & Register
                  </button>
                  
                  <span className="auth-toggle-link" onClick={() => switchAuthMode('login')}>Already have an account? Sign In</span>
                </form>
              )}

              {/* FLOW C: FORGOT PASSWORD MODE */}
              {authMode === 'forgot-password' && (
                <form onSubmit={handleResetPassword}>
                  <div className="form-group">
                    <label>Registered Email Address</label>
                    <div className="otp-group">
                      <div className="input-icon-wrapper" style={{ flexGrow: 1 }}>
                        <input 
                          type="email" 
                          className="form-input" 
                          style={{ paddingLeft: '14px' }}
                          placeholder="name@company.com"
                          value={resetEmail}
                          onChange={(e) => setResetEmail(e.target.value)}
                          required 
                        />
                      </div>
                      <button 
                        type="button" 
                        className="btn-secondary" 
                        onClick={handleSendResetOtp}
                        disabled={resetOtpLoading}
                      >
                        {resetOtpLoading ? 'Sending...' : 'Send OTP'}
                      </button>
                    </div>
                  </div>

                  {resetOtpSent && (
                    <div style={{ animation: 'fadeIn 0.3s ease-out' }}>
                      <div className="form-group">
                        <label>Recovery OTP Code</label>
                        <div className="input-icon-wrapper">
                          <Lock className="input-icon" />
                          <input 
                            type="text" 
                            className="form-input" 
                            placeholder="Enter recovery code"
                            value={resetOtp}
                            onChange={(e) => setResetOtp(e.target.value)}
                            required 
                          />
                        </div>
                      </div>

                      <div className="form-group">
                        <label>New Password</label>
                        <div className="input-icon-wrapper">
                          <Lock className="input-icon" />
                          <input 
                            type="password" 
                            className="form-input" 
                            placeholder="Enter new password"
                            value={resetPasswordVal}
                            onChange={(e) => setResetPasswordVal(e.target.value)}
                            required 
                          />
                        </div>
                      </div>
                    </div>
                  )}

                  <button type="submit" className="btn-primary" disabled={!resetOtpSent}>
                    Reset Password
                  </button>
                  
                  <span className="auth-toggle-link" onClick={() => switchAuthMode('login')}>Back to Sign In</span>
                </form>
              )}
            </div>
          </div>
        )}
      </div>
    );
  }

  // --- RENDER FULL DASHBOARD LAYOUT ---
  return (
    <div className="dashboard-layout">
      {/* 1. SIDEBAR NAVIGATION */}
      <aside className="sidebar">
        <div className="sidebar-logo text-gradient">
          <Sparkles /> Medi-AI-Sales
        </div>
        <ul className="nav-links">
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'inventory' ? 'active' : ''}`}
              onClick={() => setActiveTab('inventory')}
            >
              <Database size={18} /> Stock Inventory
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'customers' ? 'active' : ''}`}
              onClick={() => setActiveTab('customers')}
            >
              <Users size={18} /> Customer Profiles
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'billing' ? 'active' : ''}`}
              onClick={() => setActiveTab('billing')}
            >
              <FileText size={18} /> Billing Portal
            </button>
          </li>
          <li className="nav-item">
            <button
              className={`nav-link ${activeTab === 'ai-chat' ? 'active' : ''}`}
              onClick={() => setActiveTab('ai-chat')}
            >
              <MessageSquare size={18} /> AI Chat Assistant
            </button>
          </li>
        </ul>
        <button className="nav-link logout-btn" onClick={handleLogout}>
          <LogOut size={18} /> Sign Out
        </button>
      </aside>

      {/* 2. MAIN PANEL CONTENT */}
      <main className="main-content">
        <header className="content-header">
          <h1>{activeTab.charAt(0).toUpperCase() + activeTab.slice(1).replace('-', ' ')}</h1>
          <div className="user-tag">
            <User size={16} /> Welcome, {username}
          </div>
        </header>

        {/* --- TAB A: STOCK INVENTORY --- */}
        {activeTab === 'inventory' && (
          <div className="table-panel glass-panel">
            <div className="table-header-row">
              <div className="search-bar">
                <Search className="search-icon" />
                <input
                  type="text"
                  placeholder="Search products..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Product Name</th>
                    <th>Company</th>
                    <th>Qty Available</th>
                    <th>Rate (INR)</th>
                    <th>GST</th>
                    <th>Discount</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {products
                    .filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()))
                    .map(p => (
                      <tr key={p.id}>
                        <td>{p.id}</td>
                        <td><strong>{p.name}</strong></td>
                        <td>{p.company}</td>
                        <td>{p.quantity} units</td>
                        <td>Rs. {p.rate}</td>
                        <td>{p.gstPercentage}%</td>
                        <td>{p.discountPercentage}%</td>
                        <td>
                          {p.quantity > 10 ? (
                            <span className="badge badge-success">In Stock</span>
                          ) : (
                            <span className="badge badge-danger">Low Stock</span>
                          )}
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* --- TAB B: CUSTOMER PROFILES --- */}
        {activeTab === 'customers' && (
          <div className="table-panel glass-panel">
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Customer Name</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Address</th>
                  </tr>
                </thead>
                <tbody>
                  {customers.map(c => (
                    <tr key={c.id}>
                      <td>{c.id}</td>
                      <td><strong>{c.name}</strong></td>
                      <td>{c.phone}</td>
                      <td>{c.email || 'N/A'}</td>
                      <td>{c.address || 'N/A'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* --- TAB C: BILLING PORTAL --- */}
        {activeTab === 'billing' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: '30px' }}>
            {/* Products Selector */}
            <div className="table-panel glass-panel">
              <h2 style={{ marginBottom: '15px', fontSize: '18px' }}>Select Products to Bill</h2>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Qty Available</th>
                      <th>Rate</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {products.map(p => (
                      <tr key={p.id}>
                        <td><strong>{p.name}</strong></td>
                        <td>{p.quantity} units</td>
                        <td>Rs. {p.rate}</td>
                        <td>
                          <button
                            className="btn-primary"
                            style={{ padding: '6px 12px', fontSize: '12px', width: 'auto' }}
                            onClick={() => addToCart(p)}
                            disabled={p.quantity <= 0}
                          >
                            <Plus size={14} /> Add
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Shopping Cart Summary */}
            <div className="table-panel glass-panel" style={{ height: 'fit-content' }}>
              <h2 style={{ marginBottom: '15px', fontSize: '18px' }}>Billing Summary</h2>

              {/* Customer Selector */}
              <div className="form-group">
                <label>Select Customer</label>
                <select
                  className="form-input"
                  style={{ paddingLeft: '12px' }}
                  value={selectedCustomerId}
                  onChange={(e) => setSelectedCustomerId(e.target.value)}
                >
                  <option value="">-- Choose Customer --</option>
                  {customers.map(c => (
                    <option key={c.id} value={c.id}>{c.name} ({c.phone})</option>
                  ))}
                </select>
              </div>

              {/* Cart List */}
              <div style={{ margin: '20px 0', borderBottom: '1px solid var(--border-color)', paddingBottom: '15px' }}>
                <h3 style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '10px' }}>Cart Items</h3>
                {cart.length === 0 ? (
                  <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>Cart is empty.</p>
                ) : (
                  cart.map(item => (
                    <div key={item.productId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px', fontSize: '14px' }}>
                      <span>{item.name} (x{item.quantity})</span>
                      <span>Rs. {(item.rate * item.quantity).toFixed(2)}</span>
                    </div>
                  ))
                )}
              </div>

              <button
                className="btn-primary"
                onClick={handleGenerateInvoice}
                disabled={!selectedCustomerId || cart.length === 0}
              >
                <Download size={16} /> Generate & Print Invoice
              </button>
            </div>
          </div>
        )}

        {/* --- TAB D: AI CHAT ASSISTANT --- */}
        {activeTab === 'ai-chat' && (
          <div className="chat-container glass-panel">
            <div className="chat-history">
              {chatHistory.map((chat, idx) => (
                <div key={idx} className={`chat-bubble ${chat.sender}`}>
                  {chat.text}
                </div>
              ))}
              {aiLoading && <div className="chat-bubble ai">AI is thinking...</div>}
            </div>
            <form onSubmit={handleSendMessage} className="chat-input-area">
              <input
                type="text"
                placeholder="Ask about inventory (e.g. 'what is low in stock?')"
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
              />
              <button type="submit">
                <Send size={18} />
              </button>
            </form>
          </div>
        )}
      </main>
    </div>
  );
}