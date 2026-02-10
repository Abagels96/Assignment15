// --- API Configuration ---
const API_BASE_URL = '/selfcare';

// --- Progress Page: Summary & Chart ---
async function loadSummary() {
    const cardContainer = document.getElementById('summary-cards');
    const chartContainer = document.getElementById('chart-container');
    cardContainer.innerHTML = '<p id="summary-loading" class="text-center text-gray-400 col-span-3">Loading summary...</p>';
    chartContainer.innerHTML = ''; // Clear old chart

    try {
        // Fetch summary first
        const summaryResponse = await fetch(`${API_BASE_URL}/summary`, { credentials: 'include' });
        
        if (!summaryResponse.ok) {
            if (summaryResponse.status === 401) {
                cardContainer.innerHTML = '<p class="text-center text-red-500 col-span-3">Please log in to view your progress.</p>';
                return;
            }
            const errorText = await summaryResponse.text();
            throw new Error(`Failed to load summary (${summaryResponse.status}): ${errorText}`);
        }
        
        const summary = await summaryResponse.json();
        console.log('Summary loaded:', summary);
        
        // Try to fetch goals (requires auth, but don't fail if it doesn't work)
        let goals = { EAT: 3, SLEEP: 1, SHOWER: 1 }; // Default goals
        try {
            const goalsResponse = await fetch(`${API_BASE_URL}/goals/daily`, { credentials: 'include' });
            if (goalsResponse.ok) {
                goals = await goalsResponse.json();
                console.log('Goals loaded:', goals);
            } else {
                console.warn('Could not load goals, using defaults. Status:', goalsResponse.status);
            }
        } catch (goalsError) {
            console.warn('Error loading goals, using defaults:', goalsError);
            // Continue with default goals
        }
        
        renderSummaryCards(summary.last24Hours, goals);
        renderChart(summary.last7Days);

    } catch (error) {
        console.error('Failed to load summary:', error);
        cardContainer.innerHTML = `<p class="text-center text-red-500 col-span-3">Failed to load summary: ${error.message}</p>`;
    }
}

function renderSummaryCards(data24h, goals) {
    const container = document.getElementById('summary-cards');
    container.innerHTML = ''; // Clear loading

    const icons = { EAT: '🍎', SLEEP: '🛌', SHOWER: '🚿' };
    const colors = {
        EAT: { text: 'text-[#FA8072]', bg: 'bg-[#FA8072]' },
        SLEEP: { text: 'text-[#E37383]', bg: 'bg-[#E37383]' },
        SHOWER: { text: 'text-[#FF6F61]', bg: 'bg-[#FF6F61]' }
    };

    ['EAT', 'SLEEP', 'SHOWER'].forEach(type => {
        const count = data24h[type] || 0;
        const goal = goals[type] || 1; // Default to 1 if goal not set
        const progressPercent = Math.min(Math.round((count / goal) * 100), 100);
        const isComplete = count >= goal;
        
        const card = document.createElement('div');
        card.className = 'p-4 bg-[#FFF5F7] border-2 border-[#FFE8ED] rounded-xl shadow-lg';
        
        card.innerHTML = `
            <div class="text-center mb-3">
                <span class="text-4xl">${icons[type]}</span>
                <p class="text-sm text-gray-500 font-medium mt-1">${type}</p>
            </div>
            <div class="text-center mb-2">
                <p class="text-3xl font-bold ${colors[type].text}">${count} / ${goal}</p>
                <p class="text-xs text-gray-500 mt-1">${progressPercent}% of goal</p>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="${colors[type].bg} h-2 rounded-full transition-all duration-500 ${isComplete ? 'bg-green-500' : ''}" 
                     style="width: ${progressPercent}%"></div>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderChart(data7d) {
    const chartData = [
        { type: 'Eat', count: data7d.EAT || 0, color: '#FA8072' }, // salmon
        { type: 'Sleep', count: data7d.SLEEP || 0, color: '#E37383' }, // rose
        { type: 'Shower', count: data7d.SHOWER || 0, color: '#FF6F61' } // coral
    ];

    const container = document.getElementById('chart-container');
    container.innerHTML = ''; // Clear previous chart

    // D3 Chart Setup
    const margin = { top: 20, right: 20, bottom: 30, left: 40 };
    const width = container.clientWidth - margin.left - margin.right;
    const height = container.clientHeight - margin.top - margin.bottom;

    const svg = d3.select(container)
        .append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('viewBox', `0 0 ${container.clientWidth} ${container.clientHeight}`)
        .append('g')
        .attr('transform', `translate(${margin.left}, ${margin.top})`);

    // X scale (band for names)
    const xScale = d3.scaleBand()
        .domain(chartData.map(d => d.type))
        .range([0, width])
        .padding(0.4);

    // Y scale (linear for count)
    const yScale = d3.scaleLinear()
        .domain([0, Math.max(10, d3.max(chartData, d => d.count))]) // Min height of 10
        .range([height, 0]);

    // X Axis
    svg.append('g')
        .attr('transform', `translate(0, ${height})`)
        .call(d3.axisBottom(xScale))
        .selectAll('text')
        .style('font-size', '12px')
        .style('font-family', 'Inter');

    // Y Axis
    svg.append('g')
        .call(d3.axisLeft(yScale).ticks(5))
        .selectAll('text')
        .style('font-size', '12px')
        .style('font-family', 'Inter');

    // Bars
    svg.selectAll('.bar')
        .data(chartData)
        .enter()
        .append('rect')
        .attr('x', d => xScale(d.type))
        .attr('y', d => yScale(0)) // Start at 0 for animation
        .attr('width', xScale.bandwidth())
        .attr('height', 0) // Start at 0 for animation
        .attr('fill', d => d.color)
        .attr('rx', 4) // Rounded corners
        .transition()
        .duration(750)
        .attr('y', d => yScale(d.count))
        .attr('height', d => height - yScale(d.count));
}

// --- Attribute-Based Tracking ---
async function loadAttributeData() {
    console.log('loadAttributeData called');
    const type = document.getElementById('attribute-type').value;
    const period = document.getElementById('attribute-period').value;
    const container = document.getElementById('attribute-container');
    const loading = document.getElementById('attribute-loading');
    
    console.log('Type:', type, 'Period:', period);
    
    // Show loading
    if (loading) {
        loading.style.display = 'block';
        loading.textContent = 'Loading...';
    }
    container.innerHTML = '<p id="attribute-loading" class="text-center text-gray-400">Loading...</p>';
    
    try {
        const url = `${API_BASE_URL}/attributes?type=${type}&period=${period}`;
        console.log('Fetching from:', url);
        const response = await fetch(url);
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Response error:', errorText);
            throw new Error(`Network response was not ok: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('Received data:', data);
        
        // Render based on activity type
        if (type === 'SLEEP') {
            renderSleepQualityChart(data);
        } else if (type === 'EAT') {
            renderMealsList(data);
        } else if (type === 'SHOWER') {
            renderShowerAverageDisplay(data);
        }
        
    } catch (error) {
        console.error('Failed to load attribute data:', error);
        container.innerHTML = '<p class="text-center text-red-500">Failed to load data. Check console for details.</p>';
    }
}

function renderSleepQualityChart(data) {
    const container = document.getElementById('attribute-container');
    container.innerHTML = '';
    
    // Create header with average duration
    const header = document.createElement('div');
    header.className = 'mb-6';
    header.innerHTML = `
        <h3 class="text-lg font-bold text-indigo-700 mb-2">Sleep Quality Distribution</h3>
        <p class="text-2xl font-bold text-indigo-900">Average Duration: ${data.averageSleepDurationHours ? data.averageSleepDurationHours.toFixed(1) : '0.0'} hours</p>
    `;
    container.appendChild(header);
    
    // Prepare data for chart
    const qualities = ['EXCELLENT', 'GOOD', 'FAIR', 'POOR'];
    const colors = {
        'EXCELLENT': '#10b981',  // green
        'GOOD': '#3b82f6',       // blue
        'FAIR': '#f59e0b',       // amber
        'POOR': '#ef4444'        // red
    };
    
    const chartData = qualities.map(q => ({
        quality: q,
        count: data.sleepQualityCounts ? (data.sleepQualityCounts[q] || 0) : 0,
        color: colors[q]
    }));
    
    // Create chart container
    const chartDiv = document.createElement('div');
    chartDiv.className = 'w-full';
    chartDiv.style.height = '300px';
    container.appendChild(chartDiv);
    
    // D3 Chart Setup
    const margin = { top: 20, right: 20, bottom: 60, left: 100 };
    const width = chartDiv.clientWidth - margin.left - margin.right;
    const height = 300 - margin.top - margin.bottom;
    
    const svg = d3.select(chartDiv)
        .append('svg')
        .attr('width', '100%')
        .attr('height', '100%')
        .attr('viewBox', `0 0 ${chartDiv.clientWidth} 300`)
        .append('g')
        .attr('transform', `translate(${margin.left}, ${margin.top})`);
    
    // Y scale (band for quality names) - horizontal bars
    const yScale = d3.scaleBand()
        .domain(chartData.map(d => d.quality))
        .range([0, height])
        .padding(0.3);
    
    // X scale (linear for count)
    const maxCount = Math.max(5, d3.max(chartData, d => d.count));
    const xScale = d3.scaleLinear()
        .domain([0, maxCount])
        .range([0, width]);
    
    // Y Axis
    svg.append('g')
        .call(d3.axisLeft(yScale))
        .selectAll('text')
        .style('font-size', '14px')
        .style('font-family', 'Inter')
        .style('font-weight', '600');
    
    // X Axis
    svg.append('g')
        .attr('transform', `translate(0, ${height})`)
        .call(d3.axisBottom(xScale).ticks(5))
        .selectAll('text')
        .style('font-size', '12px')
        .style('font-family', 'Inter');
    
    // Bars
    svg.selectAll('.bar')
        .data(chartData)
        .enter()
        .append('rect')
        .attr('x', 0)
        .attr('y', d => yScale(d.quality))
        .attr('width', 0) // Start at 0 for animation
        .attr('height', yScale.bandwidth())
        .attr('fill', d => d.color)
        .attr('rx', 4)
        .transition()
        .duration(750)
        .attr('width', d => xScale(d.count));
    
    // Add count labels
    svg.selectAll('.label')
        .data(chartData)
        .enter()
        .append('text')
        .attr('x', d => xScale(d.count) + 5)
        .attr('y', d => yScale(d.quality) + yScale.bandwidth() / 2)
        .attr('dy', '.35em')
        .style('font-size', '14px')
        .style('font-family', 'Inter')
        .style('font-weight', '600')
        .text(d => d.count);
}

function renderMealsList(data) {
    const container = document.getElementById('attribute-container');
    container.innerHTML = '';
    
    // Create header
    const header = document.createElement('div');
    header.className = 'mb-6';
    header.innerHTML = `
        <h3 class="text-lg font-bold text-green-700 mb-2">Meal History</h3>
        <p class="text-2xl font-bold text-green-900">Total Meals: ${data.mealCount || 0}</p>
    `;
    container.appendChild(header);
    
    // Check if there are meals
    if (!data.meals || data.meals.length === 0) {
        container.innerHTML += '<p class="text-center text-gray-500">No meals recorded in this period.</p>';
        return;
    }
    
    // Create scrollable list
    const listContainer = document.createElement('div');
    listContainer.className = 'space-y-3 max-h-96 overflow-y-auto';
    
    data.meals.forEach(meal => {
        const mealCard = document.createElement('div');
        mealCard.className = 'p-4 bg-white rounded-lg border border-green-200 shadow-sm';
        
        // Format timestamp
        const timestamp = new Date(meal.timestamp);
        const timeStr = timestamp.toLocaleString('en-US', {
            month: 'short',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        });
        
        mealCard.innerHTML = `
            <div class="flex justify-between items-start">
                <div class="flex-1">
                    <p class="font-semibold text-gray-800">${meal.description || 'No description'}</p>
                </div>
                <span class="text-sm text-gray-500 ml-4 flex-shrink-0">${timeStr}</span>
            </div>
        `;
        
        listContainer.appendChild(mealCard);
    });
    
    container.appendChild(listContainer);
}

function renderShowerAverageDisplay(data) {
    const container = document.getElementById('attribute-container');
    container.innerHTML = '';
    
    const avgLength = data.averageShowerLengthMinutes || 0;
    
    // Create large display card
    const card = document.createElement('div');
    card.className = 'flex flex-col items-center justify-center py-12';
    card.innerHTML = `
        <div class="text-6xl mb-4">🚿</div>
        <h3 class="text-lg font-bold text-sky-700 mb-2">Average Shower Length</h3>
        <p class="text-5xl font-bold text-sky-900 mb-2">${avgLength.toFixed(1)}</p>
        <p class="text-xl text-gray-600">minutes</p>
    `;
    
    container.appendChild(card);
}

// --- Daily Track Progress ---
async function loadTrackProgress() {
    const container = document.getElementById('track-container');
    const loading = document.getElementById('track-loading');
    const dateInput = document.getElementById('track-date');
    
    // Get selected date or default to today
    let date = dateInput ? dateInput.value : '';
    if (!date) {
        const today = new Date();
        date = today.toISOString().split('T')[0];
        if (dateInput) {
            dateInput.value = date;
        }
    }
    
    // Show loading
    if (loading) {
        loading.style.display = 'block';
        loading.textContent = 'Loading daily progress...';
    }
    container.innerHTML = '<p id="track-loading" class="text-center text-gray-400">Loading daily progress...</p>';
    
    try {
        const url = `${API_BASE_URL}/track?date=${date}`;
        const response = await fetch(url, {
            credentials: 'include'
        });
        
        if (!response.ok) {
            if (response.status === 401) {
                container.innerHTML = '<p class="text-center text-red-500">Please log in to view your progress.</p>';
                return;
            }
            throw new Error(`Network response was not ok: ${response.status}`);
        }
        
        const tracks = await response.json();
        console.log('Received track data:', tracks);
        
        renderTrackProgress(tracks, date);
        
    } catch (error) {
        console.error('Failed to load track data:', error);
        container.innerHTML = '<p class="text-center text-red-500">Failed to load daily progress. Check console for details.</p>';
    }
}

function renderTrackProgress(tracks, date) {
    const container = document.getElementById('track-container');
    container.innerHTML = '';
    
    // Create header
    const header = document.createElement('div');
    header.className = 'mb-6';
    const dateObj = new Date(date);
    const formattedDate = dateObj.toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
    header.innerHTML = `
        <h3 class="text-lg font-bold text-purple-700 mb-2">Progress for ${formattedDate}</h3>
        <p class="text-sm text-gray-600">Total Accomplishments: ${tracks.length}</p>
    `;
    container.appendChild(header);
    
    if (tracks.length === 0) {
        container.innerHTML += '<p class="text-center text-gray-500 py-8">No activities tracked for this date.</p>';
        return;
    }
    
    // Group tracks by activity type
    const tracksByType = {
        'EAT': [],
        'SLEEP': [],
        'SHOWER': []
    };
    
    tracks.forEach(track => {
        const activityType = track.activity?.type || 'UNKNOWN';
        if (tracksByType[activityType]) {
            tracksByType[activityType].push(track);
        }
    });
    
    // Create progress cards for each activity type
    const icons = { EAT: '🍎', SLEEP: '🛌', SHOWER: '🚿' };
    const colors = {
        EAT: { bg: 'bg-green-50', border: 'border-green-200', text: 'text-green-700' },
        SLEEP: { bg: 'bg-indigo-50', border: 'border-indigo-200', text: 'text-indigo-700' },
        SHOWER: { bg: 'bg-sky-50', border: 'border-sky-200', text: 'text-sky-700' }
    };
    
    // Fetch goals for comparison
    fetch(`${API_BASE_URL}/goals/daily`, { credentials: 'include' })
        .then(response => response.json())
        .then(goals => {
            ['EAT', 'SLEEP', 'SHOWER'].forEach(type => {
                const typeTracks = tracksByType[type] || [];
                const doneCount = typeTracks.filter(t => t.status === 'DONE').length;
                const goal = goals[type] || 1; // Default to 1 if goal not set
                const progressPercent = Math.min(Math.round((doneCount / goal) * 100), 100);
                const isComplete = doneCount >= goal;
                
                const card = document.createElement('div');
                card.className = `p-4 rounded-lg border-2 ${colors[type].bg} ${colors[type].border} mb-4`;
                
                card.innerHTML = `
                    <div class="flex items-center justify-between mb-3">
                        <div class="flex items-center gap-3">
                            <span class="text-3xl">${icons[type]}</span>
                            <div>
                                <h4 class="font-bold ${colors[type].text} text-lg">${type}</h4>
                                <p class="text-sm text-gray-600">${doneCount} / ${goal} completed</p>
                            </div>
                        </div>
                        <div class="text-right">
                            <p class="text-2xl font-bold ${colors[type].text}">${progressPercent}%</p>
                            <p class="text-xs text-gray-500">of goal</p>
                        </div>
                    </div>
                    <div class="w-full bg-gray-200 rounded-full h-3 mb-2">
                        <div class="${isComplete ? 'bg-green-500' : colors[type].bg.replace('50', '400')} h-3 rounded-full transition-all duration-500" 
                             style="width: ${progressPercent}%"></div>
                    </div>
                    <div class="text-sm text-gray-700">
                        <span class="font-semibold">Goal:</span> ${goal} ${type.toLowerCase()}${goal === 1 ? '' : 's'} per day
                    </div>
                `;
                
                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error('Failed to load goals:', error);
            // Fallback: show without goals
            ['EAT', 'SLEEP', 'SHOWER'].forEach(type => {
                const typeTracks = tracksByType[type] || [];
                const doneCount = typeTracks.filter(t => t.status === 'DONE').length;
                
                const card = document.createElement('div');
                card.className = `p-4 rounded-lg border-2 ${colors[type].bg} ${colors[type].border} mb-4`;
                
                card.innerHTML = `
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-3">
                            <span class="text-3xl">${icons[type]}</span>
                            <div>
                                <h4 class="font-bold ${colors[type].text} text-lg">${type}</h4>
                                <p class="text-sm text-gray-600">${doneCount} completed</p>
                            </div>
                        </div>
                    </div>
                `;
                
                container.appendChild(card);
            });
        });
}

// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    loadSummary();
    loadTrackProgress();
    
    // Set up track date selector
    const trackDateInput = document.getElementById('track-date');
    if (trackDateInput) {
        trackDateInput.addEventListener('change', loadTrackProgress);
    }
    
    // Set up attribute tracking event listeners
    const typeSelect = document.getElementById('attribute-type');
    const periodSelect = document.getElementById('attribute-period');
    
    if (typeSelect && periodSelect) {
        typeSelect.addEventListener('change', loadAttributeData);
        periodSelect.addEventListener('change', loadAttributeData);
        
        // Load initial data
        loadAttributeData();
    }
});

