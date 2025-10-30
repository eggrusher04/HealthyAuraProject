import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; // Ensure axios is installed: npm install axios

const AdminDashboard = () => {
    const [activityLogs, setActivityLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Retrieve username from localStorage immediately
    const adminUsername = localStorage.getItem('adminUsername');

    useEffect(() => {
        const token = localStorage.getItem('adminToken');
        if (!token) {
            navigate('/admin/auth');
            return;
        }
        fetchActivityLogs(token);
    }, [navigate]);

    const fetchActivityLogs = async (token) => {
        try {
            const response = await axios.get('http://localhost:8080/api/admin/activity-logs', {
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            setActivityLogs(response.data);
        } catch (err) {
            console.error('Error fetching activity logs:', err);
            if (err.response && err.response.status === 401) {
                localStorage.removeItem('adminToken');
                navigate('/admin/auth');
            }
            setError('Failed to fetch activity logs');
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        const token = localStorage.getItem('adminToken');
        if (token) {
            try {
                // Call the backend endpoint to log the logout time
                await axios.post('http://localhost:8080/api/admin/auth/logout', null, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });
                console.log('Admin logout activity logged.');
            } catch (error) {
                console.error('Error logging out on backend:', error);
            }
        }
        
        // Client-side cleanup
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUsername');
        navigate('/'); // Redirect to the main page or admin login
    };

    const formatDate = (dateString) => {
        if (!dateString) return "N/A";
        try {
            return new Date(dateString).toLocaleString('en-US', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: true,
            }).replace(',', ''); 
        } catch (e) {
            return "Invalid Date";
        }
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-lg">Loading...</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header - Sizing is maintained */}
            <header className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
                        {/* Use the locally retrieved username for the welcome message */}
                        <p className="text-sm text-gray-600">
                            Welcome, {adminUsername}
                        </p>
                    </div>
                    <button
                        onClick={handleLogout}
                        className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded"
                    >
                        Logout
                    </button>
                </div>
            </header>

            {/* Main Content - Sizing is maintained */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded mb-6">
                        {error}
                    </div>
                )}

                {/* Activity Logs Section */}
                <div className="bg-white shadow rounded-lg">
                    <div className="px-6 py-4 border-b border-gray-200">
                        <h2 className="text-lg font-medium text-gray-900">Admin Activity Logs</h2>
                        <p className="mt-1 text-sm text-gray-600">
                            Recent activities performed by admins
                        </p>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Admin
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Action
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Description
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        IP Address
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Timestamp
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {activityLogs.map((log) => {
                                    // Calculate the formatted timestamp once
                                    const formattedTime = formatDate(log.timestamp);
                                    
                                    // 1. FIX: Determine the displayed username
                                    // Prioritize username from the log object, but fall back to the currently logged-in admin's username if missing.
                                    const displayAdminName = log.admin?.username || adminUsername || 'N/A';
                                    
                                    // 2. FIX: Simplify the description for LOGIN/LOGOUT actions
                                    let displayDescription = log.description;
                                    if (log.action === 'LOGIN') {
                                        displayDescription = `Admin successfully logged in`;
                                    } else if (log.action === 'LOGOUT') {
                                        displayDescription = `Admin successfully logged out`;
                                    }

                                    return (
                                        <tr key={log.id}>
                                            {/* Use the corrected admin name */}
                                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                                {displayAdminName} 
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {log.action}
                                            </td>
                                            {/* Use the corrected, simpler description */}
                                            <td className="px-6 py-4 text-sm text-gray-500">
                                                {displayDescription}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {log.ipAddress}
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                                {formattedTime}
                                            </td>
                                        </tr>
                                    );
                                })}
                                {activityLogs.length === 0 && (
                                    <tr>
                                        <td colSpan="5" className="px-6 py-4 text-center text-sm text-gray-500">
                                            No activity logs found
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminDashboard;