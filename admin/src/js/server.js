// 获取服务器列表
async function fetchServerList() {
    const refreshBtn = document.querySelector('.refresh-btn');
    if (refreshBtn) {
        refreshBtn.classList.add('loading');
    }
    try {
        const response = await fetch('http://192.168.101.80:8080/admin/serverList');
        const data = await response.json();
        if (data.gameList) {
            displayServerList(data.gameList);
        }
    } catch (error) {
        console.error('获取服务器列表失败:', error);
        alert('获取服务器列表失败，请刷新页面重试！');
    }
}

// 添加刷新函数
function refreshServerList() {
    fetchServerList();
}

// 显示服务器列表
function displayServerList(servers) {
    const serverList = document.getElementById('serverList');
    serverList.innerHTML = ''; // 清空现有内容

    servers.forEach(server => {
        const serverItem = document.createElement('div');
        serverItem.className = 'server-item';
        serverItem.innerHTML = `
            <div class="server-field">
                <label>ID:</label>
                <input type="text" value="${server.id}" readonly>
            </div>
            <div class="server-field">
                <label>IP:</label>
                <input type="text" value="${server.ip}" readonly>
            </div>
            <div class="server-field">
                <label>端口:</label>
                <input type="text" value="${server.port}" readonly>
            </div>
            <div class="server-field">
                <label>名称:</label>
                <input type="text" value="${server.name}">
            </div>
            <div class="server-field">
                <label>是否推荐:</label>
                <input type="text" value="${server.recommend}">
            </div>
            <div class="server-field">
                <label>状态:</label>
                <select>
                    <option value="0" ${parseInt(server.adminStatus) === 0 ? 'selected' : ''}>关闭</option>
                    <option value="1" ${parseInt(server.adminStatus) === 1 ? 'selected' : ''}>开启</option>
                    <option value="2" ${parseInt(server.adminStatus) === 2 ? 'selected' : ''}>维护</option>
                    <option value="3" ${parseInt(server.adminStatus) === 3 ? 'selected' : ''}>繁忙</option>
                    <option value="4" ${parseInt(server.adminStatus) === 4 ? 'selected' : ''}>火爆</option>
                </select>
            </div>
            <button class="update-btn" onclick="updateServer(this)">更新</button>
        `;
        serverList.appendChild(serverItem);
    });
}

// 更新服务器信息
async function updateServer(button) {
    const serverItem = button.parentElement;
    const inputs = serverItem.getElementsByTagName('input');
    const statusSelect = serverItem.querySelector('select');

    const serverData = {
        id: inputs[0].value,
        ip: inputs[1].value,
        port: inputs[2].value,
        name: inputs[3].value,
        recommend: inputs[4].value,
        adminStatus: parseInt(statusSelect.value, 10) 
    };

    try {
        const response = await fetch('http://192.168.101.80:8080/admin/updateServerInfo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(serverData)
        });

        const data = await response.json();
        if (data.code === 200) {
            alert('更新成功！');
        } else {
            alert(data.msg || '更新失败！');
        }
    } catch (error) {
        console.error('更新服务器信息失败:', error);
        alert('更新失败，请重试！');
    }
}

// 退出登录
function logout() {
    alert('退出登录成功！');
    window.location.href = './index.html';
}

// 页面加载完成后获取服务器列表
document.addEventListener('DOMContentLoaded', fetchServerList);
