package com.arctany.aivirtualassistant.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 3D处理工具类
 * 提供3D模型和WebGL相关功能，支持高质量渲染和物理效果
 */
@Slf4j
public class ThreeDUtils {
    
    /**
     * 高级ThreeJS HTML模板，支持物理效果和高质量渲染
     */
    private static final String THREE_JS_TEMPLATE = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>高质量3D Avatar</title>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/build/three.min.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/loaders/GLTFLoader.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/loaders/DRACOLoader.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/controls/OrbitControls.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/ammo.js@0.0.10/ammo.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/postprocessing/EffectComposer.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/postprocessing/RenderPass.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/postprocessing/ShaderPass.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/postprocessing/SSAOPass.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/shaders/CopyShader.js\"></script>\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/shaders/SSAOShader.js\"></script>\n" +
            "    <style>\n" +
            "        body { margin: 0; overflow: hidden; }\n" +
            "        canvas { display: block; }\n" +
            "        #info { position: absolute; bottom: 10px; width: 100%; text-align: center; color: #ffffff; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"info\">高质量3D头像 - 支持GPU加速</div>\n" +
            "    %s\n" +
            "</body>\n" +
            "</html>";
    
    /**
     * 创建一个基本的3D头像HTML文件
     * 
     * @param outputPath 输出HTML文件路径
     * @param avatarModel 是否加载3D模型（若为null则创建基本球体）
     * @param modelPath 3D模型路径（若avatarModel为true）
     * @return 是否创建成功
     */
    public static boolean createBasic3DAvatar(String outputPath, Boolean avatarModel, String modelPath) {
        try {
            // 创建包含目录
            Path path = Paths.get(outputPath);
            Files.createDirectories(path.getParent());
            
            // 根据参数选择脚本内容
            String scriptContent;
            if (avatarModel != null && avatarModel && modelPath != null) {
                // 使用3D模型文件
                scriptContent = createModelLoaderScript(modelPath);
            } else {
                // 使用基本球体
                scriptContent = createBasicSphereScript();
            }
            
            // 结合模板创建HTML
            String htmlContent = String.format(THREE_JS_TEMPLATE, scriptContent);
            
            // 写入文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(htmlContent);
            }
            
            log.info("成功创建3D头像HTML文件: {}", outputPath);
            return true;
        } catch (IOException e) {
            log.error("创建3D头像HTML文件失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 创建加载3D模型的JavaScript脚本，支持物理效果和高质量渲染
     * 
     * @param modelPath 3D模型路径
     * @return JavaScript脚本内容
     */
    private static String createModelLoaderScript(String modelPath) {
        return "<script>\n" +
               "    // 全局变量\n" +
               "    let scene, camera, renderer, avatar;\n" +
               "    let composer, controls;\n" +
               "    let physicsWorld, tmpTrans;\n" +
               "    let rigidBodies = [];\n" +
               "    let clock = new THREE.Clock();\n" +
               "    let usePhysics = true;\n" +
               "    \n" +
               "    // 物理世界配置\n" +
               "    const collisionConfiguration = new Ammo.btDefaultCollisionConfiguration();\n" +
               "    const dispatcher = new Ammo.btCollisionDispatcher(collisionConfiguration);\n" +
               "    const overlappingPairCache = new Ammo.btDbvtBroadphase();\n" +
               "    const solver = new Ammo.btSequentialImpulseConstraintSolver();\n" +
               "    \n" +
               "    // 主初始化函数\n" +
               "    function init() {\n" +
               "        // 检测WebGL支持\n" +
               "        if (!THREE.WEBGL.isWebGLAvailable()) {\n" +
               "            document.body.appendChild(THREE.WEBGL.getWebGLErrorMessage());\n" +
               "            return;\n" +
               "        }\n" +
               "        \n" +
               "        // 初始化物理引擎\n" +
               "        initPhysics();\n" +
               "        \n" +
               "        // 创建场景\n" +
               "        scene = new THREE.Scene();\n" +
               "        scene.background = new THREE.Color(0x112233);\n" +
               "        \n" +
               "        // 创建PBR环境贴图\n" +
               "        const envMapTexture = new THREE.CubeTextureLoader().load([\n" +
               "            'https://threejs.org/examples/textures/cube/Park3Med/px.jpg',\n" +
               "            'https://threejs.org/examples/textures/cube/Park3Med/nx.jpg',\n" +
               "            'https://threejs.org/examples/textures/cube/Park3Med/py.jpg',\n" +
               "            'https://threejs.org/examples/textures/cube/Park3Med/ny.jpg',\n" +
               "            'https://threejs.org/examples/textures/cube/Park3Med/pz.jpg',\n" +
               "            'https://threejs.org/examples/textures/cube/Park3Med/nz.jpg'\n" +
               "        ]);\n" +
               "        scene.environment = envMapTexture;\n" +
               "        \n" +
               "        // 创建高级相机\n" +
               "        camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);\n" +
               "        camera.position.set(0, 1.5, 4);\n" +
               "        \n" +
               "        // 创建高级渲染器\n" +
               "        renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });\n" +
               "        renderer.setSize(window.innerWidth, window.innerHeight);\n" +
               "        renderer.shadowMap.enabled = true;\n" +
               "        renderer.shadowMap.type = THREE.PCFSoftShadowMap;\n" +
               "        renderer.toneMapping = THREE.ACESFilmicToneMapping;\n" +
               "        renderer.toneMappingExposure = 1.0;\n" +
               "        renderer.outputEncoding = THREE.sRGBEncoding;\n" +
               "        renderer.physicallyCorrectLights = true;\n" +
               "        document.body.appendChild(renderer.domElement);\n" +
               "        \n" +
               "        // 添加轨道控制\n" +
               "        controls = new THREE.OrbitControls(camera, renderer.domElement);\n" +
               "        controls.enableDamping = true;\n" +
               "        controls.dampingFactor = 0.05;\n" +
               "        \n" +
               "        // 添加高质量灯光\n" +
               "        addLights();\n" +
               "        \n" +
               "        // 添加地面\n" +
               "        createGround();\n" +
               "        \n" +
               "        // 加载3D模型\n" +
               "        loadAvatarModel('" + modelPath + "');\n" +
               "        \n" +
               "        // 设置后期处理\n" +
               "        setupPostprocessing();\n" +
               "        \n" +
               "        // 监听窗口变化\n" +
               "        window.addEventListener('resize', onWindowResize, false);\n" +
               "        \n" +
               "        // 开始动画循环\n" +
               "        animate();\n" +
               "    }\n" +
               "    \n" +
               "    // 初始化物理引擎\n" +
               "    function initPhysics() {\n" +
               "        physicsWorld = new Ammo.btDiscreteDynamicsWorld(\n" +
               "            dispatcher, overlappingPairCache, solver, collisionConfiguration\n" +
               "        );\n" +
               "        physicsWorld.setGravity(new Ammo.btVector3(0, -9.8, 0));\n" +
               "        tmpTrans = new Ammo.btTransform();\n" +
               "    }\n" +
               "    \n" +
               "    // 添加物体到物理世界\n" +
               "    function createRigidBody(object, physicsShape, mass, pos, quat) {\n" +
               "        if (pos) {\n" +
               "            object.position.copy(pos);\n" +
               "        } else {\n" +
               "            pos = object.position;\n" +
               "        }\n" +
               "        \n" +
               "        if (quat) {\n" +
               "            object.quaternion.copy(quat);\n" +
               "        } else {\n" +
               "            quat = object.quaternion;\n" +
               "        }\n" +
               "        \n" +
               "        const transform = new Ammo.btTransform();\n" +
               "        transform.setIdentity();\n" +
               "        transform.setOrigin(new Ammo.btVector3(pos.x, pos.y, pos.z));\n" +
               "        transform.setRotation(new Ammo.btQuaternion(quat.x, quat.y, quat.z, quat.w));\n" +
               "        \n" +
               "        const motionState = new Ammo.btDefaultMotionState(transform);\n" +
               "        const localInertia = new Ammo.btVector3(0, 0, 0);\n" +
               "        \n" +
               "        if (mass > 0) {\n" +
               "            physicsShape.calculateLocalInertia(mass, localInertia);\n" +
               "        }\n" +
               "        \n" +
               "        const rbInfo = new Ammo.btRigidBodyConstructionInfo(mass, motionState, physicsShape, localInertia);\n" +
               "        const body = new Ammo.btRigidBody(rbInfo);\n" +
               "        body.setFriction(0.5);\n" +
               "        \n" +
               "        if (mass > 0) {\n" +
               "            body.setActivationState(4); // DISABLE_DEACTIVATION\n" +
               "            body.setCollisionFlags(0); // ACTIVE_TAG\n" +
               "        } else {\n" +
               "            body.setCollisionFlags(1); // STATIC_OBJECT\n" +
               "        }\n" +
               "        \n" +
               "        physicsWorld.addRigidBody(body);\n" +
               "        object.userData.physicsBody = body;\n" +
               "        rigidBodies.push(object);\n" +
               "        \n" +
               "        return body;\n" +
               "    }\n" +
               "    \n" +
               "    // 创建地面\n" +
               "    function createGround() {\n" +
               "        const groundSize = 20;\n" +
               "        \n" +
               "        // 创建地面网格\n" +
               "        const groundGeometry = new THREE.PlaneGeometry(groundSize, groundSize, 32, 32);\n" +
               "        const groundMaterial = new THREE.MeshStandardMaterial({\n" +
               "            color: 0x333333,\n" +
               "            roughness: 0.8,\n" +
               "            metalness: 0.2,\n" +
               "            envMapIntensity: 1.0\n" +
               "        });\n" +
               "        \n" +
               "        const ground = new THREE.Mesh(groundGeometry, groundMaterial);\n" +
               "        ground.rotation.x = -Math.PI / 2;\n" +
               "        ground.position.y = -1;\n" +
               "        ground.receiveShadow = true;\n" +
               "        scene.add(ground);\n" +
               "        \n" +
               "        // 添加物理形状\n" +
               "        if (usePhysics) {\n" +
               "            const groundShape = new Ammo.btBoxShape(new Ammo.btVector3(groundSize / 2, 0.5, groundSize / 2));\n" +
               "            createRigidBody(ground, groundShape, 0, ground.position, ground.quaternion);\n" +
               "        }\n" +
               "    }\n" +
               "    \n" +
               "    // 添加高质量灯光\n" +
               "    function addLights() {\n" +
               "        // 环境光\n" +
               "        const ambientLight = new THREE.AmbientLight(0xffffff, 0.3);\n" +
               "        scene.add(ambientLight);\n" +
               "        \n" +
               "        // 主平行光（太阳光）\n" +
               "        const mainLight = new THREE.DirectionalLight(0xffffff, 1.0);\n" +
               "        mainLight.position.set(5, 10, 7.5);\n" +
               "        mainLight.castShadow = true;\n" +
               "        mainLight.shadow.mapSize.width = 2048;\n" +
               "        mainLight.shadow.mapSize.height = 2048;\n" +
               "        mainLight.shadow.camera.near = 0.5;\n" +
               "        mainLight.shadow.camera.far = 50;\n" +
               "        mainLight.shadow.camera.left = -10;\n" +
               "        mainLight.shadow.camera.right = 10;\n" +
               "        mainLight.shadow.camera.top = 10;\n" +
               "        mainLight.shadow.camera.bottom = -10;\n" +
               "        mainLight.shadow.bias = -0.0005;\n" +
               "        scene.add(mainLight);\n" +
               "        \n" +
               "        // 填充光\n" +
               "        const fillLight = new THREE.DirectionalLight(0x8888ff, 0.6);\n" +
               "        fillLight.position.set(-5, 5, -5);\n" +
               "        scene.add(fillLight);\n" +
               "        \n" +
               "        // 轮廓光\n" +
               "        const rimLight = new THREE.DirectionalLight(0xffffaa, 0.2);\n" +
               "        rimLight.position.set(0, 5, -10);\n" +
               "        scene.add(rimLight);\n" +
               "    }\n" +
               "    \n" +
               "    // 设置后期处理效果\n" +
               "    function setupPostprocessing() {\n" +
               "        composer = new THREE.EffectComposer(renderer);\n" +
               "        \n" +
               "        const renderPass = new THREE.RenderPass(scene, camera);\n" +
               "        composer.addPass(renderPass);\n" +
               "        \n" +
               "        const ssaoPass = new THREE.SSAOPass(scene, camera, window.innerWidth, window.innerHeight);\n" +
               "        ssaoPass.kernelRadius = 16;\n" +
               "        ssaoPass.minDistance = 0.005;\n" +
               "        ssaoPass.maxDistance = 0.1;\n" +
               "        composer.addPass(ssaoPass);\n" +
               "    }\n" +
               "    \n" +
               "    // 加载3D头像模型\n" +
               "    function loadAvatarModel(modelPath) {\n" +
               "        const loader = new THREE.GLTFLoader();\n" +
               "        \n" +
               "        // 使用DRACO压缩加载器（如果模型使用了DRACO压缩）\n" +
               "        const dracoLoader = new THREE.DRACOLoader();\n" +
               "        dracoLoader.setDecoderPath('https://www.gstatic.com/draco/v1/decoders/');\n" +
               "        loader.setDRACOLoader(dracoLoader);\n" +
               "        \n" +
               "        loader.load(modelPath, (gltf) => {\n" +
               "            avatar = gltf.scene;\n" +
               "            \n" +
               "            // 启用阴影\n" +
               "            avatar.traverse((node) => {\n" +
               "                if (node.isMesh) {\n" +
               "                    node.castShadow = true;\n" +
               "                    node.receiveShadow = true;\n" +
               "                    \n" +
               "                    // 提高材质质量\n" +
               "                    if (node.material) {\n" +
               "                        node.material.envMapIntensity = 1.0;\n" +
               "                        node.material.needsUpdate = true;\n" +
               "                    }\n" +
               "                }\n" +
               "            });\n" +
               "            \n" +
               "            scene.add(avatar);\n" +
               "            \n" +
               "            // 适当缩放和定位模型\n" +
               "            const box = new THREE.Box3().setFromObject(avatar);\n" +
               "            const size = box.getSize(new THREE.Vector3());\n" +
               "            const maxDim = Math.max(size.x, size.y, size.z);\n" +
               "            avatar.scale.multiplyScalar(2 / maxDim);\n" +
               "            \n" +
               "            // 将模型放置在场景中央\n" +
               "            const center = box.getCenter(new THREE.Vector3());\n" +
               "            avatar.position.x = -center.x;\n" +
               "            avatar.position.y = -center.y;\n" +
               "            avatar.position.z = -center.z;\n" +
               "            \n" +
               "            // 添加物理效果（如果模型包含多个部件）\n" +
               "            if (usePhysics) {\n" +
               "                addAvatarPhysics(avatar);\n" +
               "            }\n" +
               "            \n" +
               "            // 加载动画\n" +
               "            if (gltf.animations && gltf.animations.length > 0) {\n" +
               "                avatar.mixer = new THREE.AnimationMixer(avatar);\n" +
               "                gltf.animations.forEach((clip) => {\n" +
               "                    avatar.mixer.clipAction(clip).play();\n" +
               "                });\n" +
               "            }\n" +
               "            \n" +
               "        }, undefined, (error) => {\n" +
               "            console.error('加载模型出错:', error);\n" +
               "            createAdvancedDefaultAvatar();\n" +
               "        });\n" +
               "    }\n" +
               "    \n" +
               "    // 为头像添加物理效果\n" +
               "    function addAvatarPhysics(avatarModel) {\n" +
               "        // 简化版：为整个模型添加物理碰撞体\n" +
               "        const box = new THREE.Box3().setFromObject(avatarModel);\n" +
               "        const size = box.getSize(new THREE.Vector3());\n" +
               "        \n" +
               "        const avatarShape = new Ammo.btBoxShape(new Ammo.btVector3(size.x / 2, size.y / 2, size.z / 2));\n" +
               "        const mass = 10;\n" +
               "        \n" +
               "        // 创建刚体\n" +
               "        const body = createRigidBody(avatarModel, avatarShape, mass, avatarModel.position, avatarModel.quaternion);\n" +
               "        \n" +
               "        // 可选：添加约束，限制移动范围\n" +
               "        const pivotA = new Ammo.btVector3(0, size.y / 2, 0);\n" +
               "        const pivotB = new Ammo.btVector3(0, 0, 0);\n" +
               "        \n" +
               "        const constraint = new Ammo.btPoint2PointConstraint(body, new Ammo.btVector3(0, 0, 0));\n" +
               "        physicsWorld.addConstraint(constraint, true);\n" +
               "    }\n" +
               "    \n" +
               "    // 创建高级默认头像\n" +
               "    function createAdvancedDefaultAvatar() {\n" +
               "        // 创建头部\n" +
               "        const headGeometry = new THREE.SphereGeometry(1, 64, 64);\n" +
               "        const headMaterial = new THREE.MeshStandardMaterial({\n" +
               "            color: 0x3498db,\n" +
               "            roughness: 0.3,\n" +
               "            metalness: 0.2,\n" +
               "            envMapIntensity: 1.0\n" +
               "        });\n" +
               "        \n" +
               "        avatar = new THREE.Group();\n" +
               "        const head = new THREE.Mesh(headGeometry, headMaterial);\n" +
               "        head.castShadow = true;\n" +
               "        head.receiveShadow = true;\n" +
               "        avatar.add(head);\n" +
               "        \n" +
               "        // 添加眼睛\n" +
               "        const eyeGeometry = new THREE.SphereGeometry(0.15, 32, 32);\n" +
               "        const eyeMaterial = new THREE.MeshStandardMaterial({\n" +
               "            color: 0xffffff,\n" +
               "            roughness: 0.1,\n" +
               "            metalness: 0.1\n" +
               "        });\n" +
               "        \n" +
               "        const leftEye = new THREE.Mesh(eyeGeometry, eyeMaterial);\n" +
               "        leftEye.position.set(-0.3, 0.1, 0.8);\n" +
               "        avatar.add(leftEye);\n" +
               "        \n" +
               "        const rightEye = new THREE.Mesh(eyeGeometry, eyeMaterial);\n" +
               "        rightEye.position.set(0.3, 0.1, 0.8);\n" +
               "        avatar.add(rightEye);\n" +
               "        \n" +
               "        // 添加瞳孔\n" +
               "        const pupilGeometry = new THREE.SphereGeometry(0.05, 32, 32);\n" +
               "        const pupilMaterial = new THREE.MeshStandardMaterial({ color: 0x000000 });\n" +
               "        \n" +
               "        const leftPupil = new THREE.Mesh(pupilGeometry, pupilMaterial);\n" +
               "        leftPupil.position.set(-0.3, 0.1, 0.95);\n" +
               "        avatar.add(leftPupil);\n" +
               "        \n" +
               "        const rightPupil = new THREE.Mesh(pupilGeometry, pupilMaterial);\n" +
               "        rightPupil.position.set(0.3, 0.1, 0.95);\n" +
               "        avatar.add(rightPupil);\n" +
               "        \n" +
               "        // 添加嘴巴\n" +
               "        const mouthGeometry = new THREE.TorusGeometry(0.3, 0.05, 16, 32, Math.PI);\n" +
               "        const mouthMaterial = new THREE.MeshStandardMaterial({ color: 0xff0000 });\n" +
               "        const mouth = new THREE.Mesh(mouthGeometry, mouthMaterial);\n" +
               "        mouth.position.set(0, -0.2, 0.8);\n" +
               "        mouth.rotation.set(Math.PI / 2, 0, 0);\n" +
               "        avatar.add(mouth);\n" +
               "        \n" +
               "        scene.add(avatar);\n" +
               "        \n" +
               "        // 添加物理效果\n" +
               "        if (usePhysics) {\n" +
               "            const avatarShape = new Ammo.btSphereShape(1);\n" +
               "            createRigidBody(avatar, avatarShape, 5, new THREE.Vector3(0, 0, 0), avatar.quaternion);\n" +
               "        }\n" +
               "    }\n" +
               "    \n" +
               "    // 窗口大小调整\n" +
               "    function onWindowResize() {\n" +
               "        camera.aspect = window.innerWidth / window.innerHeight;\n" +
               "        camera.updateProjectionMatrix();\n" +
               "        renderer.setSize(window.innerWidth, window.innerHeight);\n" +
               "        composer.setSize(window.innerWidth, window.innerHeight);\n" +
               "    }\n" +
               "    \n" +
               "    // 更新物理世界\n" +
               "    function updatePhysics(deltaTime) {\n" +
               "        if (!usePhysics) return;\n" +
               "        \n" +
               "        physicsWorld.stepSimulation(deltaTime, 10);\n" +
               "        \n" +
               "        // 更新渲染对象位置\n" +
               "        for (let i = 0; i < rigidBodies.length; i++) {\n" +
               "            const objThree = rigidBodies[i];\n" +
               "            const objPhys = objThree.userData.physicsBody;\n" +
               "            \n" +
               "            const ms = objPhys.getMotionState();\n" +
               "            if (ms) {\n" +
               "                ms.getWorldTransform(tmpTrans);\n" +
               "                const p = tmpTrans.getOrigin();\n" +
               "                const q = tmpTrans.getRotation();\n" +
               "                \n" +
               "                objThree.position.set(p.x(), p.y(), p.z());\n" +
               "                objThree.quaternion.set(q.x(), q.y(), q.z(), q.w());\n" +
               "            }\n" +
               "        }\n" +
               "    }\n" +
               "    \n" +
               "    // 动画循环\n" +
               "    function animate() {\n" +
               "        requestAnimationFrame(animate);\n" +
               "        \n" +
               "        const deltaTime = clock.getDelta();\n" +
               "        \n" +
               "        // 更新物理\n" +
               "        updatePhysics(deltaTime);\n" +
               "        \n" +
               "        // 更新控制器\n" +
               "        controls.update();\n" +
               "        \n" +
               "        // 更新动画混合器（如果有）\n" +
               "        if (avatar && avatar.mixer) {\n" +
               "            avatar.mixer.update(deltaTime);\n" +
               "        }\n" +
               "        \n" +
               "        // 使用后期处理渲染场景\n" +
               "        composer.render();\n" +
               "    }\n" +
               "    \n" +
               "    // 表情更新函数\n" +
               "    function updateExpression(expression) {\n" +
               "        if (!avatar) return;\n" +
               "        \n" +
               "        console.log('表情更新为:', expression);\n" +
               "        \n" +
               "        // 如果是自定义头像，查找命名的骨骼和变形目标\n" +
               "        avatar.traverse((node) => {\n" +
               "            // 处理变形目标\n" +
               "            if (node.morphTargetDictionary && node.morphTargetInfluences) {\n" +
               "                // 重置所有变形目标\n" +
               "                for (let i = 0; i < node.morphTargetInfluences.length; i++) {\n" +
               "                    node.morphTargetInfluences[i] = 0;\n" +
               "                }\n" +
               "                \n" +
               "                // 设置指定表情\n" +
               "                if (expression === 'happy' && node.morphTargetDictionary['smile']) {\n" +
               "                    node.morphTargetInfluences[node.morphTargetDictionary['smile']] = 1.0;\n" +
               "                } else if (expression === 'sad' && node.morphTargetDictionary['sad']) {\n" +
               "                    node.morphTargetInfluences[node.morphTargetDictionary['sad']] = 1.0;\n" +
               "                } else if (expression === 'angry' && node.morphTargetDictionary['angry']) {\n" +
               "                    node.morphTargetInfluences[node.morphTargetDictionary['angry']] = 1.0;\n" +
               "                } else if (expression === 'surprise' && node.morphTargetDictionary['surprise']) {\n" +
               "                    node.morphTargetInfluences[node.morphTargetDictionary['surprise']] = 1.0;\n" +
               "                }\n" +
               "            }\n" +
               "            \n" +
               "            // 对于默认头像，通过颜色和形状修改表情\n" +
               "            if (node.isMesh && node.name === 'mouth' && expression) {\n" +
               "                if (expression === 'happy') {\n" +
               "                    node.rotation.x = Math.PI / 2;\n" +
               "                    node.position.y = -0.2;\n" +
               "                } else if (expression === 'sad') {\n" +
               "                    node.rotation.x = -Math.PI / 2;\n" +
               "                    node.position.y = -0.3;\n" +
               "                } else if (expression === 'surprise') {\n" +
               "                    node.geometry = new THREE.CircleGeometry(0.2, 32);\n" +
               "                    node.position.z = 0.85;\n" +
               "                } else if (expression === 'angry') {\n" +
               "                    node.material.color.set(0x880000);\n" +
               "                } else {\n" +
               "                    node.material.color.set(0xff0000);\n" +
               "                }\n" +
               "            }\n" +
               "        });\n" +
               "        \n" +
               "        // 物理效果响应表情\n" +
               "        if (usePhysics && avatar.userData.physicsBody) {\n" +
               "            if (expression === 'happy') {\n" +
               "                // 开心时上下轻微弹跳\n" +
               "                const force = new Ammo.btVector3(0, 20, 0);\n" +
               "                avatar.userData.physicsBody.applyCentralImpulse(force);\n" +
               "            } else if (expression === 'surprise') {\n" +
               "                // 惊讶时向后跳\n" +
               "                const force = new Ammo.btVector3(0, 10, -20);\n" +
               "                avatar.userData.physicsBody.applyCentralImpulse(force);\n" +
               "            } else if (expression === 'angry') {\n" +
               "                // 生气时颤抖\n" +
               "                const force = new Ammo.btVector3(Math.random() * 10 - 5, 0, Math.random() * 10 - 5);\n" +
               "                avatar.userData.physicsBody.applyCentralImpulse(force);\n" +
               "            }\n" +
               "        }\n" +
               "    }\n" +
               "    \n" +
               "    // 初始化Ammo.js\n" +
               "    Ammo().then(() => {\n" +
               "        // 初始化场景\n" +
               "        init();\n" +
               "    });\n" +
               "</script>";
    }
    
    /**
     * 创建基础球体脚本
     * 
     * @return 基础Three.js球体HTML脚本
     */
    public static String createBasicSphereScript() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Basic Avatar</title>\n" +
                "    <style>\n" +
                "        body { margin: 0; overflow: hidden; background-color: #2a2a2a; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/build/three.min.js\"></script>\n" +
                "    <script>\n" +
                "        // 创建3D场景\n" +
                "        const scene = new THREE.Scene();\n" +
                "        scene.background = new THREE.Color(0x2a2a2a);\n" +
                "        \n" +
                "        // 创建相机\n" +
                "        const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);\n" +
                "        camera.position.z = 5;\n" +
                "        \n" +
                "        // 创建渲染器\n" +
                "        const renderer = new THREE.WebGLRenderer({ antialias: true });\n" +
                "        renderer.setSize(window.innerWidth, window.innerHeight);\n" +
                "        document.body.appendChild(renderer.domElement);\n" +
                "        \n" +
                "        // 创建球体几何体\n" +
                "        const geometry = new THREE.SphereGeometry(2, 32, 32);\n" +
                "        \n" +
                "        // 创建材质\n" +
                "        const material = new THREE.MeshPhongMaterial({\n" +
                "            color: 0x156289,\n" +
                "            emissive: 0x072534,\n" +
                "            side: THREE.DoubleSide,\n" +
                "            flatShading: false\n" +
                "        });\n" +
                "        \n" +
                "        // 创建网格\n" +
                "        const sphere = new THREE.Mesh(geometry, material);\n" +
                "        scene.add(sphere);\n" +
                "        \n" +
                "        // 添加光源\n" +
                "        const light = new THREE.DirectionalLight(0xffffff, 1);\n" +
                "        light.position.set(1, 1, 1).normalize();\n" +
                "        scene.add(light);\n" +
                "        \n" +
                "        const ambientLight = new THREE.AmbientLight(0x404040);\n" +
                "        scene.add(ambientLight);\n" +
                "        \n" +
                "        // 处理窗口大小变化\n" +
                "        window.addEventListener('resize', () => {\n" +
                "            camera.aspect = window.innerWidth / window.innerHeight;\n" +
                "            camera.updateProjectionMatrix();\n" +
                "            renderer.setSize(window.innerWidth, window.innerHeight);\n" +
                "        });\n" +
                "        \n" +
                "        // 动画循环\n" +
                "        function animate() {\n" +
                "            requestAnimationFrame(animate);\n" +
                "            \n" +
                "            sphere.rotation.x += 0.005;\n" +
                "            sphere.rotation.y += 0.01;\n" +
                "            \n" +
                "            renderer.render(scene, camera);\n" +
                "        }\n" +
                "        \n" +
                "        animate();\n" +
                "        \n" +
                "        // 表情更新接口\n" +
                "        window.updateExpression = function(expression) {\n" +
                "            console.log('Expression updated:', expression);\n" +
                "            \n" +
                "            if (expression === 'happy') {\n" +
                "                material.color.set(0x2ca02c); // 绿色\n" +
                "            } else if (expression === 'sad') {\n" +
                "                material.color.set(0x9467bd); // 紫色\n" +
                "            } else if (expression === 'thinking') {\n" +
                "                material.color.set(0xd62728); // 红色\n" +
                "            } else {\n" +
                "                material.color.set(0x156289); // 默认蓝色\n" +
                "            }\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
    
    /**
     * 创建一个简单的2D头像HTML文件
     * 
     * @param outputPath 输出HTML文件路径
     * @param imagePath 图片路径
     * @return 是否创建成功
     */
    public static boolean create2DAvatar(String outputPath, String imagePath) {
        try {
            // 创建包含目录
            Path path = Paths.get(outputPath);
            Files.createDirectories(path.getParent());
            
            // 创建HTML内容
            String htmlContent = 
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>2D Avatar</title>\n" +
                    "    <style>\n" +
                    "        body { margin: 0; overflow: hidden; display: flex; justify-content: center; align-items: center; height: 100vh; }\n" +
                    "        .avatar { width: 80%; height: auto; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <img id=\"avatar\" class=\"avatar\" src=\"" + imagePath + "\" alt=\"Avatar\">\n" +
                    "    <script>\n" +
                    "        // 暴露给Java调用的方法\n" +
                    "        function updateExpression(expression) {\n" +
                    "            // 2D形象可以通过切换图片来实现表情变化\n" +
                    "            // 这里仅作示例，实际应用需要准备多个表情图片\n" +
                    "            const avatar = document.getElementById('avatar');\n" +
                    "            if (expression === 'happy') {\n" +
                    "                avatar.style.transform = 'scale(1.1)';\n" +
                    "            } else if (expression === 'sad') {\n" +
                    "                avatar.style.transform = 'scale(0.9)';\n" +
                    "            } else {\n" +
                    "                avatar.style.transform = 'scale(1.0)';\n" +
                    "            }\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
            
            // 写入文件
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                writer.write(htmlContent);
            }
            
            log.info("成功创建2D头像HTML文件: {}", outputPath);
            return true;
        } catch (IOException e) {
            log.error("创建2D头像HTML文件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建GLTF查看器HTML
     * 
     * @param modelFileName 模型文件名
     * @return HTML内容
     */
    public static String createGLTFViewerHtml(String modelFileName) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>3D Avatar Viewer</title>\n" +
                "    <style>\n" +
                "        body { margin: 0; overflow: hidden; }\n" +
                "        canvas { display: block; }\n" +
                "        #info { position: absolute; bottom: 10px; width: 100%; text-align: center; color: #fff; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/build/three.min.js\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/loaders/GLTFLoader.js\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/three@0.150.0/examples/js/controls/OrbitControls.js\"></script>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/ammo.js@0.0.10/builds/ammo.wasm.js\"></script>\n" +
                "    <script>\n" +
                "        // 初始化物理引擎\n" +
                "        Ammo().then(function(AmmoLib) {\n" +
                "            Ammo = AmmoLib;\n" +
                "            init();\n" +
                "        });\n" +
                "\n" +
                "        let camera, scene, renderer, controls;\n" +
                "        let avatar, mixer;\n" +
                "        let clock = new THREE.Clock();\n" +
                "        \n" +
                "        function init() {\n" +
                "            // 创建场景\n" +
                "            scene = new THREE.Scene();\n" +
                "            scene.background = new THREE.Color(0x2a2a2a);\n" +
                "            \n" +
                "            // 创建环境光\n" +
                "            const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);\n" +
                "            scene.add(ambientLight);\n" +
                "            \n" +
                "            // 创建主光源\n" +
                "            const mainLight = new THREE.DirectionalLight(0xffffff, 0.8);\n" +
                "            mainLight.position.set(0, 10, 10);\n" +
                "            mainLight.castShadow = true;\n" +
                "            scene.add(mainLight);\n" +
                "            \n" +
                "            // 创建填充光源\n" +
                "            const fillLight = new THREE.DirectionalLight(0xffffff, 0.3);\n" +
                "            fillLight.position.set(-10, 5, -10);\n" +
                "            scene.add(fillLight);\n" +
                "            \n" +
                "            // 创建相机\n" +
                "            camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);\n" +
                "            camera.position.set(0, 1.7, 5);\n" +
                "            \n" +
                "            // 创建渲染器\n" +
                "            renderer = new THREE.WebGLRenderer({ antialias: true });\n" +
                "            renderer.setSize(window.innerWidth, window.innerHeight);\n" +
                "            renderer.setPixelRatio(window.devicePixelRatio);\n" +
                "            renderer.shadowMap.enabled = true;\n" +
                "            renderer.shadowMap.type = THREE.PCFSoftShadowMap;\n" +
                "            document.body.appendChild(renderer.domElement);\n" +
                "            \n" +
                "            // 创建控制器\n" +
                "            controls = new THREE.OrbitControls(camera, renderer.domElement);\n" +
                "            controls.target.set(0, 1.5, 0);\n" +
                "            controls.update();\n" +
                "            \n" +
                "            // 加载模型\n" +
                "            const loader = new THREE.GLTFLoader();\n" +
                "            loader.load('" + modelFileName + "', function(gltf) {\n" +
                "                avatar = gltf.scene;\n" +
                "                scene.add(avatar);\n" +
                "                \n" +
                "                // 设置阴影\n" +
                "                avatar.traverse(function(node) {\n" +
                "                    if (node.isMesh) {\n" +
                "                        node.castShadow = true;\n" +
                "                        node.receiveShadow = true;\n" +
                "                    }\n" +
                "                });\n" +
                "                \n" +
                "                // 设置动画\n" +
                "                if (gltf.animations && gltf.animations.length) {\n" +
                "                    mixer = new THREE.AnimationMixer(avatar);\n" +
                "                    const action = mixer.clipAction(gltf.animations[0]);\n" +
                "                    action.play();\n" +
                "                }\n" +
                "                \n" +
                "                // 自动调整相机距离\n" +
                "                const box = new THREE.Box3().setFromObject(avatar);\n" +
                "                const size = box.getSize(new THREE.Vector3()).length();\n" +
                "                const center = box.getCenter(new THREE.Vector3());\n" +
                "                \n" +
                "                controls.maxDistance = size * 10;\n" +
                "                camera.position.copy(center.clone().add(new THREE.Vector3(0, 0, size * 2)));\n" +
                "                controls.target.copy(center);\n" +
                "                controls.update();\n" +
                "            });\n" +
                "            \n" +
                "            // 创建地板\n" +
                "            const floorGeometry = new THREE.CircleGeometry(5, 32);\n" +
                "            const floorMaterial = new THREE.MeshStandardMaterial({ color: 0x404040 });\n" +
                "            const floor = new THREE.Mesh(floorGeometry, floorMaterial);\n" +
                "            floor.rotation.x = -Math.PI / 2;\n" +
                "            floor.receiveShadow = true;\n" +
                "            scene.add(floor);\n" +
                "            \n" +
                "            // 窗口大小变化时调整\n" +
                "            window.addEventListener('resize', onWindowResize);\n" +
                "            \n" +
                "            // 开始动画循环\n" +
                "            animate();\n" +
                "        }\n" +
                "        \n" +
                "        function onWindowResize() {\n" +
                "            camera.aspect = window.innerWidth / window.innerHeight;\n" +
                "            camera.updateProjectionMatrix();\n" +
                "            renderer.setSize(window.innerWidth, window.innerHeight);\n" +
                "        }\n" +
                "        \n" +
                "        function animate() {\n" +
                "            requestAnimationFrame(animate);\n" +
                "            \n" +
                "            const delta = clock.getDelta();\n" +
                "            if (mixer) mixer.update(delta);\n" +
                "            \n" +
                "            controls.update();\n" +
                "            renderer.render(scene, camera);\n" +
                "        }\n" +
                "        \n" +
                "        // 表情更新接口\n" +
                "        window.updateExpression = function(expression) {\n" +
                "            // 在这里根据表情更新模型\n" +
                "            console.log('Expression updated:', expression);\n" +
                "            \n" +
                "            if (avatar) {\n" +
                "                // 查找头部模型\n" +
                "                let head = null;\n" +
                "                avatar.traverse(function(node) {\n" +
                "                    if (node.isMesh && (node.name.includes('Head') || node.name.includes('Face'))) {\n" +
                "                        head = node;\n" +
                "                    }\n" +
                "                });\n" +
                "                \n" +
                "                if (head && head.morphTargetInfluences) {\n" +
                "                    // 清除现有表情\n" +
                "                    for (let i = 0; i < head.morphTargetInfluences.length; i++) {\n" +
                "                        head.morphTargetInfluences[i] = 0;\n" +
                "                    }\n" +
                "                    \n" +
                "                    // 应用新表情\n" +
                "                    if (expression === 'happy') {\n" +
                "                        // 查找笑脸morph target\n" +
                "                        for (let i = 0; i < head.morphTargetDictionary.length; i++) {\n" +
                "                            if (head.morphTargetDictionary[i].includes('smile') || \n" +
                "                                head.morphTargetDictionary[i].includes('happy')) {\n" +
                "                                head.morphTargetInfluences[i] = 1.0;\n" +
                "                                break;\n" +
                "                            }\n" +
                "                        }\n" +
                "                    } else if (expression === 'sad') {\n" +
                "                        // 查找难过表情\n" +
                "                        for (let i = 0; i < head.morphTargetDictionary.length; i++) {\n" +
                "                            if (head.morphTargetDictionary[i].includes('sad') || \n" +
                "                                head.morphTargetDictionary[i].includes('frown')) {\n" +
                "                                head.morphTargetInfluences[i] = 1.0;\n" +
                "                                break;\n" +
                "                            }\n" +
                "                        }\n" +
                "                    } else if (expression === 'thinking') {\n" +
                "                        // 查找思考表情\n" +
                "                        for (let i = 0; i < head.morphTargetDictionary.length; i++) {\n" +
                "                            if (head.morphTargetDictionary[i].includes('think') || \n" +
                "                                head.morphTargetDictionary[i].includes('ponder')) {\n" +
                "                                head.morphTargetInfluences[i] = 1.0;\n" +
                "                                break;\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        };\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }
} 