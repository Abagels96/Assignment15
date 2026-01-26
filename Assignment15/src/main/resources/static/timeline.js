// --- API Configuration ---
const API_BASE_URL = 'http://localhost:8080/selfcare';

// --- Activity Icons and Colors ---
const ACTIVITY_CONFIG = {
    EAT: {
        icon: '🍎',
        color: 'bg-green-500',
        textColor: 'text-green-700',
        borderColor: 'border-green-200',
        bgLight: 'bg-green-50'
    },
    SLEEP: {
        icon: '🛌',
        color: 'bg-indigo-500',
        textColor: 'text-indigo-700',
        borderColor: 'border-indigo-200',
        bgLight: 'bg-indigo-50'
    },
    SHOWER: {
        icon: '🚿',
        color: 'bg-sky-500',
        textColor: 'text-sky-700',
        borderColor: 'border-sky-200',
        bgLight: 'bg-sky-50'
    }
};

// --- Load Timeline Data ---
async function loadTimelineData() {
    const loading = document.getElementById('timeline-loading');
    const empty = document.getElementById('timeline-empty');
    const container = document.getElementById('timeline-container');
    
    try {
        const response = await fetch(`${API_BASE_URL}/timeline`);
        if (!response.ok) throw new Error('Network response was not ok');
        
        const activities = await response.json();
        
        // Hide loading
        loading.classList.add('hidden');
        
        if (activities.length === 0) {
            // Show empty state
            empty.classList.remove('hidden');
            container.classList.add('hidden');
        } else {
            // Show timeline with activities
            empty.classList.add('hidden');
            container.classList.remove('hidden');
            renderTimeline(activities);
        }
        
    } catch (error) {
        console.error('Failed to load timeline:', error);
        loading.innerHTML = '<p class="text-center text-red-500">Failed to load timeline. Please try again.</p>';
    }
}

// --- Render Timeline ---
function renderTimeline(activities) {
    const container = document.getElementById('timeline-container');
    container.innerHTML = '';
    
    // Create timeline line
    const timelineLine = document.createElement('div');
    timelineLine.className = 'timeline-line';
    container.appendChild(timelineLine);
    
    // Render each activity
    activities.forEach((activity, index) => {
        const timelineItem = createTimelineItem(activity, index);
        container.appendChild(timelineItem);
    });
}

// --- Create Timeline Item ---
function createTimelineItem(activity, index) {
    const config = ACTIVITY_CONFIG[activity.type];
    const isLeft = index % 2 === 0; // Alternate left and right
    
    const item = document.createElement('div');
    item.className = 'timeline-item relative mb-8 md:mb-12';
    
    // Format time
    const time = formatTime(activity.timestamp);
    const relativeTime = getRelativeTime(activity.timestamp);
    
    // Create timeline dot
    const dot = document.createElement('div');
    dot.className = `timeline-dot ${config.color}`;
    dot.style.top = '24px';
    
    // Create activity card
    const card = document.createElement('div');
    card.className = `activity-card ${config.bgLight} ${config.borderColor} border-2 rounded-xl p-4 md:p-6 shadow-md max-w-sm md:max-w-md ${
        isLeft ? 'md:mr-auto md:ml-0 md:pr-12' : 'md:ml-auto md:mr-0 md:pl-12'
    } mx-auto md:mx-0`;
    card.style.width = 'calc(100% - 40px)';
    card.style.maxWidth = isLeft ? 'calc(50% - 40px)' : 'calc(50% - 40px)';
    
    // On mobile, center everything
    if (window.innerWidth < 768) {
        card.style.maxWidth = '400px';
        card.classList.add('mx-auto');
    }
    
    card.onclick = () => showActivityDetails(activity);
    
    card.innerHTML = `
        <div class="flex items-start gap-4">
            <div class="text-4xl flex-shrink-0">${config.icon}</div>
            <div class="flex-1">
                <div class="flex items-center justify-between mb-1">
                    <h3 class="text-lg font-bold ${config.textColor}">${activity.type}</h3>
                    <span class="text-xs text-gray-500">${relativeTime}</span>
                </div>
                <p class="text-sm text-gray-600 font-medium mb-2">${time}</p>
                ${getActivityPreview(activity)}
            </div>
        </div>
        <div class="mt-3 text-xs text-gray-500 text-right">
            <span class="italic">Click for details →</span>
        </div>
    `;
    
    item.appendChild(dot);
    item.appendChild(card);
    
    return item;
}

// --- Get Activity Preview ---
function getActivityPreview(activity) {
    switch(activity.type) {
        case 'SLEEP':
            if (activity.quality) {
                return `<p class="text-sm text-gray-700">Quality: <span class="font-semibold">${activity.quality}</span></p>`;
            }
            return '<p class="text-sm text-gray-500">Sleep recorded</p>';
            
        case 'EAT':
            if (activity.mealDescription) {
                const truncated = activity.mealDescription.length > 50 
                    ? activity.mealDescription.substring(0, 50) + '...' 
                    : activity.mealDescription;
                return `<p class="text-sm text-gray-700">${truncated}</p>`;
            }
            return '<p class="text-sm text-gray-500">Meal recorded</p>';
            
        case 'SHOWER':
            if (activity.lengthInMinutes) {
                return `<p class="text-sm text-gray-700">Duration: <span class="font-semibold">${activity.lengthInMinutes} minutes</span></p>`;
            }
            return '<p class="text-sm text-gray-500">Shower recorded</p>';
            
        default:
            return '';
    }
}

// --- Show Activity Details in Modal ---
function showActivityDetails(activity) {
    const modal = document.getElementById('activity-modal');
    const modalBody = document.getElementById('modal-body');
    const config = ACTIVITY_CONFIG[activity.type];
    
    const time = formatTime(activity.timestamp);
    const date = formatDate(activity.timestamp);
    
    let detailsHTML = `
        <div class="text-center mb-6">
            <div class="text-6xl mb-3">${config.icon}</div>
            <h2 class="text-2xl font-bold ${config.textColor} mb-1">${activity.type}</h2>
            <p class="text-gray-500 text-sm">${date}</p>
            <p class="text-gray-700 font-semibold">${time}</p>
        </div>
        <div class="${config.bgLight} rounded-lg p-4 space-y-3">
    `;
    
    // Add activity-specific details
    switch(activity.type) {
        case 'SLEEP':
            const duration = calculateSleepDuration(activity);
            detailsHTML += `
                <div class="flex justify-between items-center">
                    <span class="font-semibold text-gray-700">Quality:</span>
                    <span class="text-lg font-bold ${config.textColor}">${activity.quality || 'Not recorded'}</span>
                </div>
            `;
            if (activity.startDateTime && activity.endDateTime) {
                detailsHTML += `
                    <div class="flex justify-between items-center">
                        <span class="font-semibold text-gray-700">Start:</span>
                        <span class="text-gray-600">${formatDateTime(activity.startDateTime)}</span>
                    </div>
                    <div class="flex justify-between items-center">
                        <span class="font-semibold text-gray-700">End:</span>
                        <span class="text-gray-600">${formatDateTime(activity.endDateTime)}</span>
                    </div>
                    <div class="flex justify-between items-center pt-2 border-t border-gray-200">
                        <span class="font-semibold text-gray-700">Duration:</span>
                        <span class="text-lg font-bold ${config.textColor}">${duration}</span>
                    </div>
                `;
            }
            break;
            
        case 'EAT':
            detailsHTML += `
                <div>
                    <span class="font-semibold text-gray-700 block mb-2">Meal Description:</span>
                    <p class="text-gray-600 bg-white rounded p-3">${activity.mealDescription || 'No description provided'}</p>
                </div>
            `;
            break;
            
        case 'SHOWER':
            detailsHTML += `
                <div class="flex justify-between items-center">
                    <span class="font-semibold text-gray-700">Duration:</span>
                    <span class="text-lg font-bold ${config.textColor}">${activity.lengthInMinutes || 'Not recorded'} minutes</span>
                </div>
            `;
            if (activity.rating) {
                detailsHTML += `
                    <div class="flex justify-between items-center">
                        <span class="font-semibold text-gray-700">Rating:</span>
                        <span class="text-lg font-bold ${config.textColor}">${activity.rating}</span>
                    </div>
                `;
            }
            break;
    }
    
    detailsHTML += `</div>`;
    
    modalBody.innerHTML = detailsHTML;
    modal.classList.add('show');
}

// --- Close Modal ---
function closeModal() {
    const modal = document.getElementById('activity-modal');
    modal.classList.remove('show');
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    const modal = document.getElementById('activity-modal');
    if (e.target === modal) {
        closeModal();
    }
});

// Close modal on Escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeModal();
    }
});

// --- Utility Functions ---

function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('en-US', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    });
}

function formatDate(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function formatDateTime(dateTimeString) {
    // Handle LocalDateTime from backend
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    });
}

function getRelativeTime(timestamp) {
    const now = new Date();
    const activityTime = new Date(timestamp);
    const diffMs = now - activityTime;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    return 'Earlier';
}

function calculateSleepDuration(activity) {
    if (!activity.startDateTime || !activity.endDateTime) {
        return 'Not recorded';
    }
    
    const start = new Date(activity.startDateTime);
    const end = new Date(activity.endDateTime);
    const diffMs = end - start;
    const hours = Math.floor(diffMs / 3600000);
    const minutes = Math.floor((diffMs % 3600000) / 60000);
    
    if (hours > 0 && minutes > 0) {
        return `${hours}h ${minutes}m`;
    } else if (hours > 0) {
        return `${hours}h`;
    } else {
        return `${minutes}m`;
    }
}

// --- Handle Window Resize ---
let resizeTimer;
window.addEventListener('resize', () => {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(() => {
        // Reload timeline on significant resize (mobile <-> desktop)
        const container = document.getElementById('timeline-container');
        if (!container.classList.contains('hidden')) {
            loadTimelineData();
        }
    }, 250);
});

// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    loadTimelineData();
});

