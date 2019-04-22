console.log('i am content script');

const port = chrome.runtime.connect();

port.onMessage.addListener(m => {
    console.log('received message', m);
    if (m.type === 'server event' && m.message === 'next') {
        document.querySelector('#player-bar-forward').click()
    } else if (m.type === 'server event' && m.message === 'prev') {
        document.querySelector('#player-bar-rewind').click()
    }
});

