function getAllVisibleFbVideo() {
    try {
        // Function to check if the element is visible
        function isVisible(elem) {
            if (!elem) return false;
            var style = window.getComputedStyle(elem);
            if (style.display === 'none' || style.visibility === 'hidden' || style.opacity === '0') return false;
            var rect = elem.getBoundingClientRect();
            return rect.width > 0 && rect.height > 0 && rect.bottom >= 0 && rect.top <= window.innerHeight && rect.right >= 0 && rect.left <= window.innerWidth;
        }

        // Function to process element attributes
        function processElementAttributes(element) {
            var videoData = {
                videoUrl: element.getAttribute('data-video-url'),
                imageUrl: null,
                dashManifest: null
            };

            var img = element.querySelector('img');
            if (img && img.src) {
                videoData.imageUrl = img.src
            }

            var dash = JSON.parse(element.getAttribute('data-extra')).dash_manifest;
            if (dash) {
                videoData.dashManifest = dash
            }
            return videoData;
        }

        // Get all elements with data-type='video'
        var allVideoElements = document.querySelectorAll('[data-type="video"]');

        // Filter visible elements and map to their details
        var visibleVideoElementsDetails = Array.prototype.filter.call(allVideoElements, isVisible)
            .map(processElementAttributes);

        // Return the JSON representation of the elements' details
        return JSON.stringify(visibleVideoElementsDetails);
    } catch (error) {
        return JSON.stringify({error: error.message});
    }
}



function getAllVisibleIGVideo() {
    const videos = document.querySelectorAll('video');
    const visibleVideosData = Array.from(videos).filter(video => {
        const rect = video.getBoundingClientRect();
        const isVisible = (
            rect.top >= 0 &&
            rect.left >= 0 &&
            rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
            rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        );

        return isVisible && getComputedStyle(video).display !== 'none';
    }).map(video => {
        return {
            videoUrl: video.currentSrc || video.src,
            imageUrl: null,
            dashManifest: null
            // Add more properties as needed
        };
    });

    return JSON.stringify(visibleVideosData);
}




