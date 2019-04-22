let listeners = [];

chrome.runtime.onConnect.addListener(port => {
    listeners = [...listeners, port];
    console.log('connected', port);
    port.onDisconnect.addListener(() => {
        listeners = listeners.filter(l => l !== port);
    });
});

function connect() {
    console.log('connecting');
    const socket = new WebSocket("ws://localhost:8080/play-pause-controller");
    let closeCalled = false;

    function reconnect() {
        socket.close();
        setTimeout(() => {
            if (!closeCalled) {
                closeCalled = true;
                connect();
            }
        }, 5000);
    }

    socket.onmessage = e => {
        console.log(`received message, dispatching to ${listeners.length} listeners`, e);
        listeners.forEach(l => {
            l.postMessage({ type: 'server event', message: e.data }, () => {
                if (chrome.runtime.lastError) console.error(chrome.runtime.lastError)
            });
        });
    };

    socket.onerror = reconnect;
    socket.onclose = () => {
        console.log('connection close');
        reconnect();
    };
}

connect();
