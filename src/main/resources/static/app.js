// Local Storage Key
const LOCAL_STORAGE_KEY = 'zippy_shortened_links';

// Document Elements
const shortenTab = document.getElementById('shorten-tab');
const analyticsTab = document.getElementById('analytics-tab');
const tabButtons = document.querySelectorAll('.tab-btn');

// Page Load initialization
document.addEventListener('DOMContentLoaded', () => {
    renderHistory();
});

// Tab Switcher
function switchTab(tabName) {
    tabButtons.forEach(btn => btn.classList.remove('active'));
    
    if (tabName === 'shorten') {
        document.querySelector("[onclick=\"switchTab('shorten')\"]").classList.add('active');
        shortenTab.classList.add('active');
        analyticsTab.classList.remove('active');
    } else {
        document.querySelector("[onclick=\"switchTab('analytics')\"]").classList.add('active');
        analyticsTab.classList.add('active');
        shortenTab.classList.remove('active');
    }
}

// Handle Shortening Request
async function handleShorten(event) {
    event.preventDefault();
    
    const longUrlInput = document.getElementById('long-url');
    const submitBtn = document.getElementById('shorten-submit');
    const resultBox = document.getElementById('result-box');
    const shortenedUrlInput = document.getElementById('shortened-url');
    const qrCodeImg = document.getElementById('qr-code-img');
    
    const originalUrl = longUrlInput.value.trim();
    if (!originalUrl) return;

    // Loading State
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span>Generating...</span><i class="fa-solid fa-circle-notch fa-spin"></i>';

    try {
        const response = await fetch('/api/v1/urls/shorten', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ originalUrl: originalUrl })
        });

        if (!response.ok) {
            throw new Error('Failed to shorten URL');
        }

        const data = await response.json();
        
        // Show Result
        shortenedUrlInput.value = data.shortUrl;
        
        // Generate QR Code using free api
        const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(data.shortUrl)}`;
        qrCodeImg.src = qrApiUrl;
        
        resultBox.classList.remove('hide');
        
        // Save to History
        addLinkToHistory(data);
        
        // Reset Form
        longUrlInput.value = '';
        
    } catch (error) {
        console.error(error);
        alert('An error occurred while shortening the URL. Please try again.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<span>Shorten Link</span><i class="fa-solid fa-arrow-right"></i>';
    }
}

// Handle Analytics Tracking Request
async function handleAnalytics(event) {
    event.preventDefault();
    
    const shortKeyInput = document.getElementById('short-key');
    const submitBtn = document.getElementById('analytics-submit');
    const resultBox = document.getElementById('analytics-result');
    
    const shortKey = shortKeyInput.value.trim();
    if (!shortKey) return;

    // Loading State
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span>Fetching...</span><i class="fa-solid fa-circle-notch fa-spin"></i>';
    resultBox.classList.add('hide');

    try {
        const response = await fetch(`/api/v1/urls/${shortKey}/analytics`);

        if (response.status === 404) {
            alert('Short URL key not found. Please check and try again.');
            return;
        }
        
        if (!response.ok) {
            throw new Error('Failed to fetch analytics');
        }

        const data = await response.json();
        
        // Update Stats
        document.getElementById('stat-clicks').innerText = data.clicksCount;
        
        // Format date nicely
        const dateObj = new Date(data.createdAt);
        document.getElementById('stat-date').innerText = dateObj.toLocaleString();
        
        const originalUrlAnchor = document.getElementById('stat-original-url');
        originalUrlAnchor.innerText = data.originalUrl;
        originalUrlAnchor.href = data.originalUrl;
        
        resultBox.classList.remove('hide');
        
        // Also update local storage if it's there
        updateLinkClicksInHistory(shortKey, data.clicksCount);
        
    } catch (error) {
        console.error(error);
        alert('An error occurred while fetching analytics. Please try again.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<span>Track Link</span><i class="fa-solid fa-chart-line"></i>';
    }
}

// Copy URL to Clipboard
function copyToClipboard(inputId, buttonId) {
    const inputElement = document.getElementById(inputId);
    const buttonElement = document.getElementById(buttonId);
    
    inputElement.select();
    inputElement.setSelectionRange(0, 99999); // For mobile devices
    
    navigator.clipboard.writeText(inputElement.value).then(() => {
        // Copied state
        buttonElement.classList.add('copied');
        buttonElement.innerHTML = '<i class="fa-solid fa-check"></i>';
        
        setTimeout(() => {
            // Revert state
            buttonElement.classList.remove('copied');
            buttonElement.innerHTML = '<i class="fa-regular fa-copy"></i>';
        }, 2000);
    }).catch(err => {
        console.error('Could not copy text: ', err);
    });
}

// Local Storage History Management
function getHistory() {
    const raw = localStorage.getItem(LOCAL_STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
}

function saveHistory(history) {
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(history));
}

function addLinkToHistory(linkData) {
    let history = getHistory();
    // Prevent duplicates
    history = history.filter(item => item.shortKey !== linkData.shortKey);
    // Add to top of list
    history.unshift(linkData);
    // Limit to 10 items
    if (history.length > 10) history.pop();
    
    saveHistory(history);
    renderHistory();
}

function updateLinkClicksInHistory(shortKey, newClicks) {
    let history = getHistory();
    let updated = false;
    history = history.map(item => {
        if (item.shortKey === shortKey) {
            item.clicksCount = newClicks;
            updated = true;
        }
        return item;
    });
    if (updated) {
        saveHistory(history);
        renderHistory();
    }
}

async function refreshClicks(shortKey) {
    const refreshBtn = document.getElementById(`refresh-${shortKey}`);
    if (refreshBtn) {
        refreshBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i>';
    }
    
    try {
        const response = await fetch(`/api/v1/urls/${shortKey}/analytics`);
        if (response.ok) {
            const data = await response.json();
            updateLinkClicksInHistory(shortKey, data.clicksCount);
        }
    } catch (e) {
        console.error(e);
    }
}

function deleteHistoryItem(shortKey) {
    let history = getHistory();
    history = history.filter(item => item.shortKey !== shortKey);
    saveHistory(history);
    renderHistory();
}

function clearHistory() {
    if (confirm('Are you sure you want to clear your local URL history?')) {
        localStorage.removeItem(LOCAL_STORAGE_KEY);
        renderHistory();
    }
}

function renderHistory() {
    const historyCard = document.getElementById('history-card');
    const tbody = document.getElementById('history-body');
    const history = getHistory();
    
    if (history.length === 0) {
        historyCard.classList.add('hide');
        return;
    }
    
    historyCard.classList.remove('hide');
    tbody.innerHTML = '';
    
    history.forEach(item => {
        const row = document.createElement('tr');
        
        row.innerHTML = `
            <td>
                <a href="${item.shortUrl}" class="hist-short-link" target="_blank">
                    ${item.shortKey}
                </a>
            </td>
            <td>
                <span class="hist-long-link" title="${item.originalUrl}">
                    ${item.originalUrl}
                </span>
            </td>
            <td>
                <span class="hist-clicks">${item.clicksCount}</span>
            </td>
            <td style="display: flex; gap: 0.75rem;">
                <button class="btn-table-action" id="refresh-${item.shortKey}" onclick="refreshClicks('${item.shortKey}')" title="Refresh Clicks">
                    <i class="fa-solid fa-arrows-rotate"></i>
                </button>
                <button class="btn-table-action" onclick="deleteHistoryItem('${item.shortKey}')" title="Delete">
                    <i class="fa-regular fa-trash-can" style="color: var(--error-color);"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}
