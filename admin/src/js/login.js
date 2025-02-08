// 生成验证码
function generateCaptcha() {
    const captcha = Math.random().toString(36).substring(2, 6).toUpperCase(); // 生成4位随机验证码
    document.getElementById('captcha-container').innerText = `验证码: ${captcha}`; // 更新页面上的验证码
    return captcha;
}

// 初始化验证码
let currentCaptcha = generateCaptcha();

// 刷新验证码
function refreshCaptcha() {
    currentCaptcha = generateCaptcha();
}

// 登录功能
async function login() {
    const accountId = document.getElementById('accountId').value; // 获取账号
    const password = document.getElementById('password').value; // 获取密码
    const captcha = document.getElementById('captcha').value; // 获取用户输入的验证码
    const errorMsgElement = document.getElementById('error-msg');

    // 输入验证
    if (!accountId || !password) {
        errorMsgElement.innerText = '请填写完整的登录信息！';
        return;
    }

    if (isNaN(accountId)) {
        errorMsgElement.innerText = '账号必须是数字！';
        return;
    }

    // 验证验证码
    if (captcha !== currentCaptcha) {
        errorMsgElement.innerText = '验证码错误，请重新输入！';
        currentCaptcha = generateCaptcha(); // 重新生成验证码
        return;
    }

    // 禁用登录按钮
    const loginButton = document.querySelector('button[type="submit"]');
    if (loginButton) loginButton.disabled = true;

    try {
        // 发送登录请求
        const response = await fetch('http://192.168.101.21:8080/admin/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                accountId: parseInt(accountId),
                password: password
            })
        });

        const data = await response.json();

        if (data.code === 200) {
            window.location.href = './server.html'; // 跳转到登录成功后的页面
        } else {
            errorMsgElement.innerText = data.msg || '登录失败，请重试！';
            currentCaptcha = generateCaptcha(); // 重新生成验证码
        }
    } catch (error) {
        console.error('登录请求失败:', error);
        errorMsgElement.innerText = '网络错误，请稍后重试！';
        currentCaptcha = generateCaptcha(); // 重新生成验证码
    } finally {
        // 恢复登录按钮
        if (loginButton) loginButton.disabled = false;
    }
}
