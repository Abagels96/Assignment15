// --- API Configuration ---
const API_BASE_URL = 'http://localhost:8080/selfcare';

// --- Helper Functions ---
function getActivityColor(type) {
    switch (type) {
        case 'EAT': return 'bg-green-100 text-green-800 border-green-300';
        case 'SLEEP': return 'bg-indigo-100 text-indigo-800 border-indigo-300';
        case 'SHOWER': return 'bg-sky-100 text-sky-800 border-sky-300';
        default: return 'bg-gray-100 text-gray-700 border-gray-300';
    }
}

function formatTimestamp(isoString) {
    const date = new Date(isoString);
    return date.toLocaleString('en-US', {
        month: 'short', day: 'numeric',
        hour: 'numeric', minute: '2-digit', hour12: true
    });
}

// --- Tracker Page: History ---
function renderHistory(activities) {
    const list = document.getElementById('history-list');
    list.innerHTML = ''; // Clear loading/stale data

    if (activities.length === 0) {
        list.innerHTML = '<p class="text-center text-gray-500 p-4 rounded-lg bg-gray-50">No activities logged yet. Get started!</p>';
        return;
    }

    // Note: Service layer now handles sorting
    // activities.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

    activities.forEach(activity => {
        const item = document.createElement('div');
        const colorClass = getActivityColor(activity.type);

        item.className = `flex justify-between items-center p-3 border rounded-lg ${colorClass}`;
        item.innerHTML = `
            <span class="font-medium capitalize text-lg">${activity.type.toLowerCase()}</span>
            <span class="text-sm">${formatTimestamp(activity.timestamp)}</span>
        `;
        list.appendChild(item);
    });
}

async function loadHistory() {
    const list = document.getElementById('history-list');
    const loading = document.getElementById('history-loading');
    if (loading) loading.style.display = 'block';
    try {
        const response = await fetch(`${API_BASE_URL}/history`);
        if (!response.ok) throw new Error('Network response was not ok');
        const history = await response.json();
        renderHistory(history);
    } catch (error) {
        console.error('Failed to load history:', error);
        list.innerHTML = '<p class="text-center text-red-500">Failed to load history.</p>';
    }
}

async function recordActivity(activityType) {
    const newActivity = {
        type: activityType,
        timestamp: new Date().toISOString()
    };

    try {
        const response = await fetch(`${API_BASE_URL}/record`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newActivity)
        });

        if (!response.ok) throw new Error('Failed to save activity');
        
        // Reload history to show the new item
        loadHistory();

    } catch (error) {
        console.error('Network Error:', error);
        // You could show an error message to the user here
    }
}

// --- Tracker Page: Clear History Modal ---
const modal = document.getElementById('confirm-modal');
function showClearHistoryModal() {
    modal.classList.remove('hidden');
}
function cancelClear() {
    modal.classList.add('hidden');
}
async function clearHistoryConfirmed() {
    try {
         const response = await fetch(`${API_BASE_URL}/history/clear`, {
            method: 'DELETE'
         });
         if (!response.ok) throw new Error('Failed to clear history');
         
         loadHistory(); // Refresh the list (will be empty)
         cancelClear(); // Close the modal
    } catch (error) {
         console.error('Failed to clear history:', error);
         // We don't use alert()
         console.error('Error: Could not clear history.');
    }
}

// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    loadHistory(); // Load history on page load
});

