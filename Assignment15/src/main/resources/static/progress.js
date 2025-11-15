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

// --- Initial Load ---
document.addEventListener('DOMContentLoaded', () => {
    loadSummary();
});

