/** 请求服务的环境配置 */
type ServiceEnv = Record<string, {url:string,urlPattern:string}>;

/** 不同请求服务的环境配置 */
const serviceEnv: ServiceEnv = {
  dev: {
    url: 'http://localhost:80',
    urlPattern: '/api',
  },
  test: {
    url: 'http://localhost:8080',
    urlPattern: '/url-pattern'
  },
  prod: {
    url: 'http://localhost:8080',
    urlPattern: '/url-pattern'
  }
};

/**
 * 获取当前环境模式下的请求服务的配置
 * @param env 环境
 */
export function getServiceEnvConfig(env: 'dev'|'test'|'prod') {
  return  serviceEnv[env];
}
