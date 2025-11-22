// --- API Configuration ---
const API_BASE_URL = 'http://localhost:8080/selfcare';

const type= document.getElementById("action-buttons")



// --- Helper Functions ---
function getActivityColor(type) {
	console.log("here I am");
    switch (type) {
		
        case 'EAT': return 'bg-green-100 text-green-800 border-green-300';
        case 'SLEEP': return 'bg-indigo-100 text-indigo-800 border-indigo-300';
        case 'SHOWER': return 'bg-sky-100 text-sky-800 border-sky-300';
        default: return 'bg-gray-100 text-gray-700 border-gray-300';
    }
}

function formatTimestamp(isoString) {
    if (!isoString) return 'N/A';
   
    
    const date = new Date(isoString);
    if (isNaN(date.getTime())) return 'Invalid date';
    
    return date.toLocaleString('en-US', {
        month: 'short', 
        day: 'numeric',
        hour: 'numeric', 
        minute: '2-digit', 
        hour12: true
    });
}

// --- Tracker Page: History ---
function renderHistory(activities) {
    const list = document.getElementById('history-list');
    const loading = document.getElementById('history-loading');
    
    // Hide loading indicator if it exists
    if (loading) {
        loading.style.display = 'none';
    }
    
    // Clear existing content
    list.innerHTML = '';

    if (activities.length === 0) {
        list.innerHTML = '<p class="text-center text-gray-500 p-4 rounded-lg bg-gray-50">No activities logged yet. Get started!</p>';
        return;
    }

    // Note: Service layer now handles sorting
    // activities.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

    activities.forEach(activity => {
        const item = document.createElement('div');
        const colorClass = getActivityColor(activity.type);

        // For sleep activities, prefer startDateTime if available, otherwise use timestamp
        // For other activities, use timestamp
        let timeToDisplay = activity.timestamp;
		const endTimeToDisplay=null;
		const quality=null;
        if (activity.type === 'SLEEP') {
            // Convert LocalDateTime string to ISO format for formatting
            timeToDisplay = activity.startDateTime;
			console.log(timeToDisplay);
			endTimeToDisplay= activity.endDateTime;
			console.log(endTimeToDisplay);
			quality= activity.quality;
			console.log(quality);
			
			
			
			
        }

        item.className = `flex justify-between items-center p-3 border rounded-lg ${colorClass}`;
        item.innerHTML = `
            <span class="font-medium capitalize text-lg flex-shrink-0">${activity.type.toLowerCase()}</span>
            <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${formatTimestamp(timeToDisplay)}</span>
            <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${formatTimestamp(endTimeToDisplay)}</span>
            <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${(quality)}</span>
        `;
        list.appendChild(item);
    });
}

async function loadHistory() {
    const list = document.getElementById('history-list');
    let loading = document.getElementById('history-loading');
    
    // If loading element doesn't exist (was cleared), create it
    if (!loading) {
        loading = document.createElement('p');
        loading.id = 'history-loading';
        loading.className = 'text-center text-gray-400';
        loading.textContent = 'Loading history...';
        list.appendChild(loading);
    }
    
    loading.style.display = 'block';
    try {
        const response = await fetch(`${API_BASE_URL}/history`);
        if (!response.ok) throw new Error('Network response was not ok');
        const history = await response.json();
        loading.style.display = 'none';
        renderHistory(history);
    } catch (error) {
        console.error('Failed to load history:', error);
        loading.style.display = 'none';
        list.innerHTML = '<p class="text-center text-red-500">Failed to load history.</p>';
    }
}

async function recordActivity(type) {
     console.log('Sleep activity selected');
    const isSleep = type === 'SLEEP';
    const isShower = type === 'SHOWER';
    const isEat = type === 'EAT';
    
    // For sleep, show the form instead of immediately recording
    if (isSleep) {
		
       
        showSleepForm();
        return;
    }
    
    // For other activities, record immediately
    const newActivity = {
        type: type,
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

function showSleepForm() {
    const sleepFormSection = document.getElementById('sleep-form-section');
    if (sleepFormSection) {
        sleepFormSection.classList.remove('hidden');
        
        // Set default values to current date and time
        const now = new Date();
        const dateStr = now.toISOString().split('T')[0];
        const timeStr = now.toTimeString().split(' ')[0].substring(0, 5);
        
        const dateInput = document.getElementById('sleep-start-date');
        const timeInput = document.getElementById('sleep-start-time');
        
        if (dateInput && !dateInput.value) {
            dateInput.value = dateStr;
        }
        if (timeInput && !timeInput.value) {
            timeInput.value = timeStr;
        }
        
        // Scroll to form
        sleepFormSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}

function hideSleepForm() {
    const sleepFormSection = document.getElementById('sleep-form-section');
    if (sleepFormSection) {
        sleepFormSection.classList.add('hidden');
    }
}

async function submitSleep() {
    const dateInput = document.getElementById('sleep-start-date');
    const timeInput = document.getElementById('sleep-start-time');
    const qualitySelect = document.getElementById('sleep-quality');
    const endDateInput = document.getElementById('sleep-end-date');
    const endTimeInput = document.getElementById('sleep-end-time');
    if (!dateInput || !timeInput || !qualitySelect) {
        console.error('Sleep form elements not found');
        return;
    }
    
    const date = dateInput.value;
    const time = timeInput.value;
    const quality = qualitySelect.value;
    const endDate = endDateInput.value;
    const endTime = endTimeInput.value;
    
    if (!date || !time) {
        alert('Please select both date and time for sleep start');
        return;
    }
    
    // Combine date and time into LocalDateTime format (YYYY-MM-DDTHH:mm:ss)
    // This format is directly parseable by Jackson into LocalDateTime
    const startDateTime = `${date}T${time}:00`;
    const endDateTime = `${endDate}T${endTime}:00`;
    
    const sleepActivity = {
        type: 'SLEEP',
        timestamp: new Date().toISOString(), // Current time for when the record was created
        startDateTime: startDateTime,
        endDateTime: endDateTime, // Can be updated later if needed, but probably needs to start a timer that keep 
		//track of time. 
        quality: quality
    };

    try {
        const response = await fetch(`${API_BASE_URL}/record/sleep`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(sleepActivity)
        });

        if (!response.ok) throw new Error('Failed to save sleep activity');
        
        // Hide the form and reload history
        hideSleepForm();
        loadHistory();

    } catch (error) {
        console.error('Network Error:', error);
        alert('Failed to record sleep. Please try again.');
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

