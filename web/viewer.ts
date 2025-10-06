const img = document.getElementById("processed-frame") as HTMLImageElement;
const stats = document.getElementById("stats") as HTMLParagraphElement;

// Simulate FPS updates
let fps = 15;
let width = 0;
let height = 0;

// When the image loads, update its resolution
img.onload = () => {
  width = img.naturalWidth;
  height = img.naturalHeight;
  updateStats();
};

// Function to update overlay text
function updateStats() {
  stats.textContent = `FPS: ${fps} | Resolution: ${width}x${height}`;
}

// Simulate FPS fluctuations
setInterval(() => {
  fps = 10 + Math.floor(Math.random() * 10);
  updateStats();
}, 1000);
