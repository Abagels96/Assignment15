// --- API Configuration ---
const API_BASE_URL = 'http://localhost:8080/selfcare';

const type= document.getElementById("action-buttons")



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
		let endTimeToDisplay=null;
		let quality=null;
		let rating=null;
		let lengthInMinutes=null;
        if (activity.type === 'SLEEP') {
            // Convert LocalDateTime string to ISO format for formatting
            timeToDisplay = activity.startDateTime;
			endTimeToDisplay= activity.endDateTime;
			quality= activity.quality;
			
			// get validation for the endTimeToDisplay and quality to makes sure they are not null
			
			
        } else if (activity.type === 'SHOWER') {
            timeToDisplay = activity.timestamp;
            rating = activity.rating;
            lengthInMinutes = activity.lengthInMinutes;
        } else if (activity.type === 'EAT') {
            timeToDisplay = activity.timestamp;
        }

        // Build the display content based on activity type
        let displayContent = '';
        let clickableClass = '';
        
        if (activity.type === 'SLEEP') {
            displayContent = `
                <div class="flex items-center flex-1 min-w-0">
                    <span class="font-medium capitalize text-lg flex-shrink-0">${activity.type.toLowerCase()} <button onclick="event.stopPropagation(); deleteSleepActivity(${activity.id || 'null'})" class="ml-4 flex-shrink-0 text-red-600 hover:text-red-800 hover:bg-red-100 px-3 py-1.5 rounded-md transition font-medium text-base" title="Delete sleep instance">
                    ×
                </button></span>
                    <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${formatTimestamp(timeToDisplay)}${endTimeToDisplay ? " - " + formatTimestamp(endTimeToDisplay) : ""}</span>
                    <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${quality || ''}</span>
                </div>
                
            `;
        } else if (activity.type === 'SHOWER') {
            // Convert rating enum to number for display
            let ratingDisplay = 'N/A';
            if (rating) {
                ratingDisplay = rating === 'ONE' ? '1' : 
                               rating === 'TWO' ? '2' : 
                               rating === 'THREE' ? '3' : 
                               rating === 'FOUR' ? '4' : 
                               rating === 'FIVE' ? '5' : rating;
            }
            displayContent = `
                <span class="font-medium capitalize text-lg flex-shrink-0">${activity.type.toLowerCase()}</span>
                <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${formatTimestamp(timeToDisplay)}</span>
                <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">Rating: ${ratingDisplay}/5 | Length: ${lengthInMinutes || 'N/A'} min</span>
            `;
        } else if (activity.type === 'EAT') {
            // Make Eat items clickable to show meal description
            const hasMealDescription = activity.mealDescription && activity.mealDescription.trim();
            clickableClass = hasMealDescription ? 'cursor-pointer hover:opacity-80' : '';
            displayContent = `
                <span class="font-medium capitalize text-lg flex-shrink-0">${activity.type.toLowerCase()}</span>
                <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${formatTimestamp(timeToDisplay)}</span>
                ${hasMealDescription ? '<span class="text-sm ml-4 flex-shrink-0 text-green-600">Click to view meal</span>' : ''}
            `;
            if (hasMealDescription) {
                item.addEventListener('click', () => {
                    showMealDescriptionModal(activity.mealDescription);
                });
            }
        } else {
            displayContent = `
                <span class="font-medium capitalize text-lg flex-shrink-0">${activity.type.toLowerCase()}</span>
                <span class="text-sm whitespace-nowrap ml-4 flex-shrink-0">${formatTimestamp(timeToDisplay)}</span>
            `;
        }
        
        item.className = `flex justify-between items-center p-3 border rounded-lg ${colorClass} ${clickableClass}`;
        
        item.innerHTML = displayContent;
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
    
    // For shower, show the form instead of immediately recording
    if (isShower) {
        showShowerForm();
        return;
    }
    
    // For eat, show the form instead of immediately recording
    if (isEat) {
        showEatForm();
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

function showShowerForm() {
    const showerFormSection = document.getElementById('shower-form-section');
    if (showerFormSection) {
        showerFormSection.classList.remove('hidden');
        
        // Clear previous values
        const ratingInput = document.getElementById('shower-rating');
        const lengthInput = document.getElementById('shower-length');
        
        if (ratingInput) {
            ratingInput.value = 'THREE'; // Default to 3
        }
        if (lengthInput) {
            lengthInput.value = '';
        }
        
        // Scroll to form
        showerFormSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}

function hideShowerForm() {
    const showerFormSection = document.getElementById('shower-form-section');
    if (showerFormSection) {
        showerFormSection.classList.add('hidden');
    }
}

function showEatForm() {
    const eatFormSection = document.getElementById('eat-form-section');
    if (eatFormSection) {
        eatFormSection.classList.remove('hidden');
        
        // Clear previous values
        const mealDescriptionInput = document.getElementById('eat-meal-description');
        
        if (mealDescriptionInput) {
            mealDescriptionInput.value = '';
        }
        
        // Scroll to form
        eatFormSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}

function hideEatForm() {
    const eatFormSection = document.getElementById('eat-form-section');
    if (eatFormSection) {
        eatFormSection.classList.add('hidden');
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
    //this looks terrible Kevin would lose his mind lets write a function for this 
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

async function submitShower() {
    const ratingSelect = document.getElementById('shower-rating');
    const lengthInput = document.getElementById('shower-length');
    
    if (!ratingSelect || !lengthInput) {
        console.error('Shower form elements not found');
        return;
    }
    
    const rating = ratingSelect.value;
    const lengthInMinutes = parseInt(lengthInput.value);
    
    if (!rating) {
        alert('Please select a rating');
        return;
    }
    
    if (!lengthInMinutes || lengthInMinutes < 1) {
        alert('Please enter a valid length in minutes (at least 1 minute)');
        return;
    }
    
    const showerActivity = {
        type: 'SHOWER',
        timestamp: new Date().toISOString(),
        rating: rating,
        lengthInMinutes: lengthInMinutes
    };

    try {
        const response = await fetch(`${API_BASE_URL}/record/shower`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(showerActivity)
        });

        if (!response.ok) throw new Error('Failed to save shower activity');
        
        
        // Convert rating enum to number for display
        const ratingNumber = rating === 'ONE' ? '1' : 
                            rating === 'TWO' ? '2' : 
                            rating === 'THREE' ? '3' : 
                            rating === 'FOUR' ? '4' : 
                            rating === 'FIVE' ? '5' : rating;
        
        // Show success message with rating and length
        alert(`Shower recorded!\nRating: ${ratingNumber}/5\nLength: ${lengthInMinutes} minutes`);
        
        // Hide the form and reload history
        hideShowerForm();
        loadHistory();

    } catch (error) {
        console.error('Network Error:', error);
        alert('Failed to record shower. Please try again.');
    }
}

async function submitEat() {
    const mealDescriptionInput = document.getElementById('eat-meal-description');
    
    if (!mealDescriptionInput) {
        console.error('Eat form elements not found');
        return;
    }
    
    const mealDescription = mealDescriptionInput.value.trim();
    
    if (!mealDescription) {
        alert('Please enter a meal description');
        return;
    }
    
    const eatActivity = {
        timestamp: new Date().toISOString(),
        mealDescription: mealDescription
    };

    try {
        const response = await fetch(`${API_BASE_URL}/record/eat`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(eatActivity)
        });

        if (!response.ok) throw new Error('Failed to save eat activity');
        
        // Hide the form and reload history
        hideEatForm();
        loadHistory();

    } catch (error) {
        console.error('Network Error:', error);
        alert('Failed to record meal. Please try again.');
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

// --- Meal Description Modal ---
function showMealDescriptionModal(mealDescription) {
    const modal = document.getElementById('meal-description-modal');
    const textElement = document.getElementById('meal-description-text');
    
    if (modal && textElement) {
        textElement.textContent = mealDescription || 'No meal description available.';
        modal.classList.remove('hidden');
    }
}

function hideMealDescriptionModal() {
    const modal = document.getElementById('meal-description-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

// --- Delete Sleep Activity ---
async function deleteSleepActivity(id) {
    if (!id) {
        console.error('Cannot delete: activity ID is missing');
        alert('Cannot delete this sleep instance: ID is missing.');
        return;
    }
    
    if (!confirm('Are you sure you want to delete this sleep instance? This action cannot be undone.')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/activity/${id}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to delete sleep activity');
        
        // Reload history to reflect the deletion
        loadHistory();
    } catch (error) {
        console.error('Failed to delete sleep activity:', error);
        alert('Failed to delete sleep instance. Please try again.');
    }
}

// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    loadHistory(); // Load history on page load
});

