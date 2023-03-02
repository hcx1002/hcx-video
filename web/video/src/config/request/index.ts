import { getServiceEnvConfig } from '../env-config';
import { createRequest } from './request';

const { url,urlPattern } = getServiceEnvConfig('dev');

export const request = createRequest({ baseURL: urlPattern});
