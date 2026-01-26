// --- API Configuration ---
const API_BASE_URL = 'http://localhost:8080/selfcare';

// --- Progress Page: Summary & Chart ---
async function loadSummary() {
    const cardContainer = document.getElementById('summary-cards');
    const chartContainer = document.getElementById('chart-container');
    cardContainer.innerHTML = '<p id="summary-loading" class="text-center text-gray-400 col-span-3">Loading summary...</p>';
    chartContainer.innerHTML = ''; // Clear old chart

    try {
        const response = await fetch(`${API_BASE_URL}/summary`);
        if (!response.ok) throw new Error('Network response was not ok');
        const summary = await response.json();
        
        renderSummaryCards(summary.last24Hours);
        renderChart(summary.last7Days);

    } catch (error) {
        console.error('Failed to load summary:', error);
        cardContainer.innerHTML = '<p class="text-center text-red-500 col-span-3">Failed to load summary.</p>';
    }
}

function renderSummaryCards(data24h) {
    const container = document.getElementById('summary-cards');
    container.innerHTML = ''; // Clear loading

    const icons = { EAT: '🍎', SLEEP: '🛌', SHOWER: '🚿' };
    const colors = {
        EAT: 'text-green-600',
        SLEEP: 'text-indigo-600',
        SHOWER: 'text-sky-600'
    };

    ['EAT', 'SLEEP', 'SHOWER'].forEach(type => {
        const count = data24h[type] || 0;
        const card = document.createElement('div');
        card.className = 'p-4 bg-gray-50 rounded-lg text-center shadow-sm';
        card.innerHTML = `
            <span class="text-4xl">${icons[type]}</span>
            <p class="text-3xl font-bold ${colors[type]}">${count}</p>
            <p class="text-sm text-gray-500 font-medium">${type}</p>
        `;
        container.appendChild(card);
    });
}

function renderChart(data7d) {
    const chartData = [
        { type: 'Eat', count: data7d.EAT || 0, color: '#10b981' }, // green-600
        { type: 'Sleep', count: data7d.SLEEP || 0, color: '#6366f1' }, // indigo-500
        { type: 'Shower', count: data7d.SHOWER || 0, color: '#0ea5e9' } // sky-500
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

// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    loadSummary();
    
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

